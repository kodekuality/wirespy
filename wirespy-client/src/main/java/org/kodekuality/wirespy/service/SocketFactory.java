package org.kodekuality.wirespy.service;

import java.io.IOException;
import java.net.Socket;

public class SocketFactory {
    private final String host;
    private final int port;

    public SocketFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Socket create() {
        try {
            return new Socket(host, port);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
