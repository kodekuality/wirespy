package org.kodekuality.wirespy.service.tcp;

import org.kodekuality.wirespy.service.tcp.socket.InitiatorSocketFactory;
import org.kodekuality.wirespy.service.tcp.socket.ListenerSocketFactory;
import org.kodekuality.wirespy.service.tcp.socket.SocketFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SpySocketFactory implements Closeable {
    public static SpySocketFactory create (String name, ServerSocket serverSocket) {
        return new SpySocketFactory(
                name,
                serverSocket.getLocalPort(),
                new ListenerSocketFactory(name, serverSocket)
        );
    }
    public static SpySocketFactory create (String name, String host, int port) {
        return new SpySocketFactory(
                name,
                port,
                new InitiatorSocketFactory(name, host, port)
        );
    }

    private final String name;
    private final int port;
    private final SocketFactory socketFactory;

    public SpySocketFactory(String name, int port, SocketFactory socketFactory) {
        this.name = name;
        this.port = port;
        this.socketFactory = socketFactory;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public Socket get() throws Exception {
        return socketFactory.create();
    }

    @Override
    public void close() throws IOException {
        socketFactory.close();
    }
}
