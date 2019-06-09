package org.kodekuality.wirespy.protocol.fix;

import org.kodekuality.wirespy.protocol.FrameSequencer;
import org.kodekuality.wirespy.protocol.Protocol;

public class FixProtocol implements Protocol {
    public static FixProtocol fix() {
        return new FixProtocol();
    }

    @Override
    public FrameSequencer getSendSequencer() {
        return new FixFrameSequencer();
    }

    @Override
    public FrameSequencer getReceiveSequencer() {
        return new FixFrameSequencer();
    }
}
