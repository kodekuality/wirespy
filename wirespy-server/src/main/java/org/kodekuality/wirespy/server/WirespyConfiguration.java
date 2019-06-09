package org.kodekuality.wirespy.server;

public class WirespyConfiguration {
    private final int port;

    public WirespyConfiguration(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
