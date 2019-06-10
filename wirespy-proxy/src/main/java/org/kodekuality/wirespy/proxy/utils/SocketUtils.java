package org.kodekuality.wirespy.proxy.utils;

import org.kodekuality.wirespy.proxy.request.Destination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketUtils {
    private static final Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    public static ServerSocket createServer (int port) {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    public static Socket createSocket (Destination destination) {
        try {
            logger.info("Trying to reach out to {}:{}", destination.getHost(), destination.getPort());
            return new Socket(destination.getHost(), destination.getPort());
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
            logger.info("Now listening for connections on {}", serverSocket.getLocalPort());
            return serverSocket.accept();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
