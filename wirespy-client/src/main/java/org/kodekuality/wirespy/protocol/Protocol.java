package org.kodekuality.wirespy.protocol;

public interface Protocol {
    FrameSequencer getSendSequencer();
    FrameSequencer getReceiveSequencer();
}
