package me.qfdk.nasicampur.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

@Slf4j
public class Outil {

    public static String getRandomPort() {
        // 生成随机可用端口
        ServerSocket s = null;
        try {
            s = new ServerSocket(0);
        } catch (IOException e) {
            System.err.println("没有可用随机端口");
        }
        String port = String.valueOf(s.getLocalPort());
        try {
            s.close();
        } catch (IOException e) {
            System.err.println("端口无法关闭");
        }
        return port;
    }

    public static String getPass(String str) {
        return new StringBuffer(str).reverse().toString();
    }

    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress Address = InetAddress.getByName(host);
        try {
            Socket socket = new Socket(Address, port);
            flag = true;
        } catch (IOException e) {
            log.info("[PORT] 端口 {} 没有占用", port);
        }
        return flag;
    }
}
