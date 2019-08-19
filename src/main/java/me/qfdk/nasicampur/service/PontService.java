package me.qfdk.nasicampur.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableAsync
public class PontService {

    public Session addPort(String sshUser, String sshPassword, int localPort, String remoteHost, int remotePort) {
        try {
            return openSSH(sshUser, sshPassword, localPort, remoteHost, remotePort);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Async
    Session openSSH(String sshUser, String sshPassword, int localPort, String remoteHost, int remotePort) throws JSchException {
        // SSH访问端口
        int sshPort = 22;
        JSch jsch = new JSch();
        Session session = jsch.getSession(sshUser, remoteHost, sshPort);
        session.setPassword(sshPassword);
        // 设置第一次登陆的时候提示，可选值：(ask | yes | no)
        session.setConfig("StrictHostKeyChecking", "no");
        session.setServerAliveInterval(10000);
        session.connect();
        // 设置SSH本地端口转发,本地转发到远程
        int assinged_port = session.setPortForwardingL(localPort, remoteHost, remotePort);
        session.setDaemonThread(true);
        // 删除本地端口的转发
        // session.delPortForwardingL(localPort);
        // 断开SSH链接
        // session.disconnect();
        // 设置SSH远程端口转发,远程转发到本地
        // session.setPortForwardingR(remotePort, remoteHost, localPort);
        log.info("localhost:" + assinged_port + " -> " + remoteHost + ":" + remotePort);
        return session;
    }

    public void deletePort(int port) {
    }
}
