package org.kodekuality.wirespy.proxy;

import org.kodekuality.wirespy.proxy.request.Destination;
import org.kodekuality.wirespy.proxy.request.Spy;
import org.kodekuality.wirespy.proxy.request.SpyRequest;
import org.kodekuality.wirespy.proxy.session.WireSpyProxySession;
import org.kodekuality.wirespy.proxy.session.WireSpyProxySessionFactory;

public class WireSpyProxy {
    private static WireSpyProxySessionFactory SESSION_FACTORY = new WireSpyProxySessionFactory();

    public static ToBuilder from (int port) {
        return new ToBuilder(port);
    }

    public static class ToBuilder {
        private int sourcePort;

        public ToBuilder(int port) {
            this.sourcePort = port;
        }

        public SpyBuilder to (String targetHost, int targetPort) {
            return new SpyBuilder(this.sourcePort, targetHost, targetPort);
        }
    }

    public static class SpyBuilder {
        private final int sourcePort;
        private final String targetHost;
        private final int targetPort;

        public SpyBuilder(int sourcePort, String targetHost, int targetPort) {
            this.sourcePort = sourcePort;
            this.targetHost = targetHost;
            this.targetPort = targetPort;
        }

        public WireSpyProxySession spyOn (int inboundPort, int outboundPort) {
            return SESSION_FACTORY.create(new SpyRequest(
                    sourcePort,
                    new Destination(targetHost, targetPort),
                    new Spy(inboundPort, outboundPort)
            ));
        }
    }
}
