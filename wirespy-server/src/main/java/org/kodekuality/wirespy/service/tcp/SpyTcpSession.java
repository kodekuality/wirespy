package org.kodekuality.wirespy.service.tcp;

public class SpyTcpSession {
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
