package org.kodekuality.wirespy.watcher.config;

public class Spy {
    private final String host;
    private final int inboundPort;
    private final int outboundPort;

    public Spy(String host, int inboundPort, int outboundPort) {
        this.host = host;
        this.inboundPort = inboundPort;
        this.outboundPort = outboundPort;
    }

    public String getHost() {
        return host;
    }

    public int getInboundPort() {
        return inboundPort;
    }

    public int getOutboundPort() {
        return outboundPort;
    }
}
