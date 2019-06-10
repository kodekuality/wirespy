package org.kodekuality.wirespy.proxy.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CopyRunnable implements Runnable {
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public CopyRunnable(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try {
            int read = inputStream.read();

            while (read != -1) {
                outputStream.write(read);
                read = inputStream.read();
            }
        } catch (IOException e) {
            // silent
        }
    }
}
