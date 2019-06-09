package org.kodekuality.wirespy.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public interface FrameSequencer {
    boolean sequence (InputStream inputStream, Consumer<Frame> receiver) throws IOException;
}
