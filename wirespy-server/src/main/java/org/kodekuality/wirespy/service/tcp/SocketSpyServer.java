package org.kodekuality.wirespy.service.tcp;

import org.kodekuality.wirespy.service.tcp.socket.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class SocketSpyServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketSpyServer.class);

    private final Thread thread;
    private final SpySocketFactory sourceSocketFactory;
    private final SpySocketFactory targetSocketFactory;
    private final ServerSocket inStreamServerSocket;
    private final ServerSocket outStreamServerSocket;

    public SocketSpyServer(Thread thread, SpySocketFactory sourceSocketFactory, SpySocketFactory targetSocketFactory, ServerSocket inStreamServerSocket, ServerSocket outStreamServerSocket) {
        this.thread = thread;
        this.sourceSocketFactory = sourceSocketFactory;
        this.targetSocketFactory = targetSocketFactory;
        this.inStreamServerSocket = inStreamServerSocket;
        this.outStreamServerSocket = outStreamServerSocket;
    }

    public SocketSpyServer start () {
        thread.start();
        return this;
    }

    public SocketSpyServer stop () {
        IOUtils.silentlyCloseCloseable(sourceSocketFactory);
        IOUtils.silentlyCloseCloseable(targetSocketFactory);
        IOUtils.silentlyCloseCloseable(inStreamServerSocket);
        IOUtils.silentlyCloseCloseable(outStreamServerSocket);

        return this;
    }
}
