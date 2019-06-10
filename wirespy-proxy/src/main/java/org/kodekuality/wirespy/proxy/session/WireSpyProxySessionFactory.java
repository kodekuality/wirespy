package org.kodekuality.wirespy.proxy.session;

import org.kodekuality.wirespy.proxy.request.SpyRequest;
import org.kodekuality.wirespy.proxy.utils.SocketUtils;

public class WireSpyProxySessionFactory {
    public WireSpyProxySession create (SpyRequest request) {
        return new ThreadWireSpyProxySession(
                SocketUtils.createServer(request.getPort()),
                SocketUtils.createServer(request.getSpy().getInbound()),
                SocketUtils.createServer(request.getSpy().getOutbound()),
                request.getDestination()
        );
    }
}
