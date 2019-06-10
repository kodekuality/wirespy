package org.kodekuality.wirespy.watcher.message;

import org.kodekuality.wirespy.protocol.Frame;

public class WireSpyMessage {
    private final long nanoTimeRecorded;
    private final String from;
    private final String to;
    private final Frame frame;

    public WireSpyMessage(long nanoTimeRecorded, String origin, String target, Frame frame) {
        this.nanoTimeRecorded = nanoTimeRecorded;
        this.from = origin;
        this.to = target;
        this.frame = frame;
    }

    public long getNanoTimeRecorded() {
        return nanoTimeRecorded;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Frame getFrame() {
        return frame;
    }

    @Override
    public String toString() {
        return "WireSpyMessage{" +
                "nanoTimeRecorded=" + nanoTimeRecorded +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", frame=" + frame +
                '}';
    }
}
