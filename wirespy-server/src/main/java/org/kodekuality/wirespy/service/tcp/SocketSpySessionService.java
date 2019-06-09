package org.kodekuality.wirespy.service.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SocketSpySessionService {
    private final List<SocketSpyServer> servers = new CopyOnWriteArrayList<>();

    private final SocketSpyServerFactory socketSpyServerFactory;

    public SocketSpySessionService(SocketSpyServerFactory socketSpyServerFactory) {
        this.socketSpyServerFactory = socketSpyServerFactory;
    }

    public SpyTcpSession startSpySession(SpySource spySource, SpyTarget spyTarget) throws IOException {
        ServerSocket sourceServerSocket = new ServerSocket(spySource.getPort());
        ServerSocket inStreamServerSocket = new ServerSocket(0);
        ServerSocket outStreamServerSocket = new ServerSocket(0);


        SocketSpyServer socketSpyServer = socketSpyServerFactory.create(
                SpySocketFactory.create(spySource.getName(), sourceServerSocket),
                SpySocketFactory.create(spyTarget.getName(), spyTarget.getHost(), spyTarget.getPort()),
                inStreamServerSocket,
                outStreamServerSocket
        );

        servers.add(socketSpyServer);
        socketSpyServer.start();

        return new SpyTcpSession(
                inStreamServerSocket.getLocalPort(),
                outStreamServerSocket.getLocalPort()
        );
    }

    public void stop() {
        List<SocketSpyServer> socketSpyServers = Collections.unmodifiableList(servers);

        for (SocketSpyServer socketSpyServer : socketSpyServers) {
            socketSpyServer.stop();
            servers.remove(socketSpyServer);
        }
    }

    public static class SpyTcpSession {
        private final int inStreamPort;
        private final int outStreamPort;

        public SpyTcpSession(int inStreamPort, int outStreamPort) {
            this.inStreamPort = inStreamPort;
            this.outStreamPort = outStreamPort;
        }

        public int getInStreamPort() {
            return inStreamPort;
        }

        public int getOutStreamPort() {
            return outStreamPort;
        }
    }

}
