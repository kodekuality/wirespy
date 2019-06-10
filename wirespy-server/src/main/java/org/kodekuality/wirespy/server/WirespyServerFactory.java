package org.kodekuality.wirespy.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.kodekuality.wirespy.admin.AdminHandler;
import org.kodekuality.wirespy.service.tcp.SpyTcpProxyService;

public class WirespyServerFactory {
    public TinyServer create (WirespyConfiguration configuration) {
        Server server = new Server(configuration.getPort());
        SpyTcpProxyService spyTcpProxyService = new SpyTcpProxyService();
        server.setHandler(new AdminHandler(new ObjectMapper(), spyTcpProxyService));
        return new TinyJettyServer(server, spyTcpProxyService);
    }

    private static class TinyJettyServer implements TinyServer {
        private final Server server;
        private SpyTcpProxyService spyTcpProxyService;

        private TinyJettyServer(Server server, SpyTcpProxyService spyTcpProxyService) {
            this.server = server;
            this.spyTcpProxyService = spyTcpProxyService;
        }

        @Override
        public TinyJettyServer start() throws Exception {
            server.start();
            return this;
        }

        @Override
        public TinyServer stop() throws Exception {
            spyTcpProxyService.close();

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
