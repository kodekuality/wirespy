package org.kodekuality.wirespy.proxy.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class MultiOutputStream extends OutputStream {
    public static MultiOutputStream multi (OutputStream... outputStreams) {
        return new MultiOutputStream(Arrays.asList(outputStreams));
    }

    private final List<OutputStream> outputStreams;

    public MultiOutputStream(List<OutputStream> outputStreams) {
        this.outputStreams = outputStreams;
    }

    @Override
    public void write(int b) throws IOException {
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(b);
        }
    }
}
