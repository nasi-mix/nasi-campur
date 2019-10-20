package me.qfdk.nasicampur.tools;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

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

    public static boolean isPortAvailable(int port) {
        try {
            ServerSocket server = new ServerSocket(port);
            server.close();
            System.out.println("The port is available.");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The port is occupied.");
        }
        return false;
    }
}
