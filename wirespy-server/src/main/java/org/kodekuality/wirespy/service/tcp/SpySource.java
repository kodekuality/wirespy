package org.kodekuality.wirespy.service.tcp;

public class SpySource {
    private final String name;
    private final int port;

    public SpySource(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
}
