package org.kodekuality.wirespy.proxy.request;

public class SpyRequest {
    private final int port;
    private final Destination destination;
    private final Spy spy;

    public SpyRequest(int port, Destination destination, Spy spy) {
        this.port = port;
        this.destination = destination;
        this.spy = spy;
    }

    public int getPort() {
        return port;
    }

    public Destination getDestination() {
        return destination;
    }

    public Spy getSpy() {
        return spy;
    }
}
