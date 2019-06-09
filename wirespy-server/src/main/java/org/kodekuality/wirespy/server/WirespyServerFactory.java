package org.kodekuality.wirespy.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.kodekuality.wirespy.admin.AdminHandler;
import org.kodekuality.wirespy.service.stream.StreamCopierFactory;
import org.kodekuality.wirespy.service.tcp.SocketAcceptService;
import org.kodekuality.wirespy.service.tcp.SocketSpyServerFactory;
import org.kodekuality.wirespy.service.tcp.SocketSpyServiceFactory;
import org.kodekuality.wirespy.service.tcp.SocketSpySessionService;

public class WirespyServerFactory {
    public TinyServer create (WirespyConfiguration configuration) {
        Server server = new Server(configuration.getPort());
        SocketSpySessionService socketSpySessionService = new SocketSpySessionService(new SocketSpyServerFactory(
                new SocketAcceptService(),
                new SocketSpyServiceFactory(new StreamCopierFactory())
        ));
        server.setHandler(new AdminHandler(new ObjectMapper(), socketSpySessionService));
        return new TinyJettyServer(server, socketSpySessionService);
    }

    private static class TinyJettyServer implements TinyServer {
        private final Server server;
        private SocketSpySessionService socketSpySessionService;

        private TinyJettyServer(Server server, SocketSpySessionService socketSpySessionService) {
            this.server = server;
            this.socketSpySessionService = socketSpySessionService;
        }

        @Override
        public TinyJettyServer start() throws Exception {
            server.start();
            return this;
        }

        @Override
        public TinyServer stop() throws Exception {
            socketSpySessionService.stop();

            server.stop();
            server.join();
            return this;
        }

        @Override
        public TinyJettyServer join() throws Exception {
            server.join();
            return this;
        }

        @Override
        public int getPort() {
            return ((ServerConnector)server.getConnectors()[0]).getLocalPort();
        }
    }
}
