package org.kodekuality.wirespy.protocol;

public class Protocol {
    private final FrameSequencer sentDataSequencer;
    private final FrameSequencer receivedDataSequencer;

    public Protocol(FrameSequencer sentDataSequencer, FrameSequencer receivedDataSequencer) {
        this.sentDataSequencer = sentDataSequencer;
        this.receivedDataSequencer = receivedDataSequencer;
    }

    public FrameSequencer getSentDataSequencer() {
        return sentDataSequencer;
    }

    public FrameSequencer getReceivedDataSequencer() {
        return receivedDataSequencer;
    }
}
