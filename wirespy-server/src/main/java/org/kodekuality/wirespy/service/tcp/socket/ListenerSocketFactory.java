package org.kodekuality.wirespy.service.tcp.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListenerSocketFactory implements SocketFactory {
    private static final Logger logger = LoggerFactory.getLogger(ListenerSocketFactory.class);

    private final List<Socket> acceptedSockets = new CopyOnWriteArrayList<>();

    private final String name;
    private final ServerSocket serverSocket;

    public ListenerSocketFactory(String name, ServerSocket serverSocket) {
        this.name = name;
        this.serverSocket = serverSocket;
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public Socket create() {
        try {
            Socket accept = serverSocket.accept();
            logger.info("Connection initiated with {} on {}", name, serverSocket.getLocalPort());
            acceptedSockets.add(accept);
            return accept;
        } catch (IOException e) {
            throw new NoLongerAcceptingException(e);
        }
    }

    @Override
    public void close() throws IOException {
        Collection<Socket> sockets = Collections.unmodifiableCollection(acceptedSockets);
        for (Socket acceptedSocket : sockets) {
            IOUtils.silentlyFlush(acceptedSocket);
            IOUtils.silentlyClose(acceptedSocket);
            acceptedSockets.remove(acceptedSocket);
        }
        serverSocket.close();
    }

    public static class NoLongerAcceptingException extends RuntimeException {
        public NoLongerAcceptingException(Throwable cause) {
            super(cause);
        }
    }
}
