package org.kodekuality.wirespy.watcher.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtils {
    public static ServerSocket createServer (int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public static Socket createSocket (String host, int port) {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public static void silentlyClose (ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public static Socket accept(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
