package me.qfdk.nasicampur.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PontService {

    public Session addPort(String sshUser, String sshPassword, String remoteHost, int remotePort) {
        return openSSH(sshUser, sshPassword, remotePort, remoteHost, remotePort);
    }

    Session openSSH(String sshUser, String sshPassword, int localPort, String remoteHost, int remotePort) {
        // SSH访问端口
        int sshPort = 22;
        JSch jsch = new JSch();
        Session session = null;
        try {

            session = jsch.getSession(sshUser, remoteHost, sshPort);
            session.setPassword(sshPassword);
            // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
            session.setConfig("StrictHostKeyChecking", "yes");
            session.setServerAliveInterval(10000);
            session.connect();
            // 设置SSH本地端口转发,本地转发到远程
            int assinged_port = session.setPortForwardingL(localPort, remoteHost, remotePort);
            // 设置SSH远程端口转发,远程转发到本地
//            session.setPortForwardingR(remotePort, remoteHost, localPort);
            session.setDaemonThread(true);
            // 删除本地端口的转发
            // session.delPortForwardingL(localPort);
            // 断开SSH链接
            // session.disconnect();
            log.info("[端口转发]添加 localhost:" + assinged_port + " -> " + remoteHost + ":" + remotePort);
        } catch (Exception e) {
            log.error(e.getMessage());
            if (session != null) {
                session.disconnect();
            }
        }
        return session;
    }

}
