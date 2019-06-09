package org.kodekuality.wirespy.service.tcp;

import org.kodekuality.wirespy.service.tcp.socket.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

public class SocketSpyServerFactory {
    private final SocketAcceptService socketAcceptService;
    private final SocketSpyServiceFactory socketSpyServiceFactory;

    public SocketSpyServerFactory(SocketAcceptService socketAcceptService, SocketSpyServiceFactory socketSpyServiceFactory) {
        this.socketAcceptService = socketAcceptService;
        this.socketSpyServiceFactory = socketSpyServiceFactory;
    }

    public SocketSpyServer create(SpySocketFactory sourceSocketFactory, SpySocketFactory targetSocketFactory, ServerSocket inStreamServerSocket, ServerSocket outStreamServerSocket) {
        SocketSpyService socketSpyService = socketSpyServiceFactory.create();
        SourceListener target = new SourceListener(socketAcceptService, socketSpyService, sourceSocketFactory, targetSocketFactory, inStreamServerSocket, outStreamServerSocket);
        Thread thread = new Thread(target, String.format("SocketSpy-%s-%s", sourceSocketFactory.getName(), targetSocketFactory.getName()));
        thread.setDaemon(true);
        return new SocketSpyServer(
                thread,
                sourceSocketFactory,
                targetSocketFactory,
                inStreamServerSocket,
                outStreamServerSocket
        );
    }

    private static class SourceListener implements Runnable {
        private static final Logger logger = LoggerFactory.getLogger(SourceListener.class);

        private final SocketAcceptService socketAcceptService;
        private final SocketSpyService socketSpyService;
        private final SpySocketFactory sourceFactory;
        private final SpySocketFactory targetFactory;
        private final ServerSocket inStreamServer;
        private final ServerSocket outStreamServer;

        public SourceListener(SocketAcceptService socketAcceptService, SocketSpyService socketSpyService, SpySocketFactory sourceFactory, SpySocketFactory targetFactory, ServerSocket inStreamServer, ServerSocket outStreamServer) {
            this.socketAcceptService = socketAcceptService;
            this.socketSpyService = socketSpyService;
            this.sourceFactory = sourceFactory;
            this.targetFactory = targetFactory;
            this.inStreamServer = inStreamServer;
            this.outStreamServer = outStreamServer;
        }

        @Override
        public void run() {
            try {
                Socket inStreamSocket = socketAcceptService.accept(sourceFactory.getName(), targetFactory.getName(), inStreamServer);
                Socket outStreamSocket = socketAcceptService.accept(targetFactory.getName(), sourceFactory.getName(), outStreamServer);

                logger.info("Ready to listen on {} ({})", sourceFactory.getPort(), sourceFactory.getName());

                socketSpyService.spy(sourceFactory, targetFactory, inStreamSocket, outStreamSocket);
            } catch (Exception e) {
                logger.debug("Error starting spy session from {} to {}", sourceFactory.getName(), targetFactory.getName(), e);
                IOUtils.silentlyCloseCloseable(inStreamServer);
                IOUtils.silentlyCloseCloseable(outStreamServer);
                IOUtils.silentlyCloseCloseable(sourceFactory);
                IOUtils.silentlyCloseCloseable(targetFactory);
            }
        }
    }
}
