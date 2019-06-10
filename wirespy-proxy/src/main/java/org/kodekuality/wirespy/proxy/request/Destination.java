package org.kodekuality.wirespy.proxy.request;

public class Destination {
    private final String host;
    private final int port;

    public Destination(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
