package org.kodekuality.wirespy.messages;

import org.kodekuality.wirespy.protocol.Frame;

public class Message {
    private final long dateCaptured;
    private final String from;
    private final String to;
    private final Frame frame;

    public Message(long dateCaptured, String from, String to, Frame frame) {
        this.dateCaptured = dateCaptured;
        this.from = from;
        this.to = to;
        this.frame = frame;
    }

    public long getDateCaptured() {
        return dateCaptured;
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
}
