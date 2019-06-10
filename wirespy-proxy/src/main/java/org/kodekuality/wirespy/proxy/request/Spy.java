package org.kodekuality.wirespy.proxy.request;

public class Spy {
    private final int inbound;
    private final int outbound;

    public Spy(int inbound, int outbound) {
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public int getInbound() {
        return inbound;
    }

    public int getOutbound() {
        return outbound;
    }
}
