package org.kodekuality.wirespy.service.stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamCopier implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(StreamCopier.class);

    private final String inputName;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public StreamCopier(String inputName, InputStream inputStream, OutputStream outputStream) {
        this.inputName = inputName;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        try (OutputStream outputStream = this.outputStream) {
            // Not optimised so information goes as fast as possible
            int read = inputStream.read();

            while (read != -1) {
                outputStream.write(read);
                read = inputStream.read();
            }

        } catch (IOException e) {
            logger.debug("Error while reading from {}", inputName, e);
        }
    }
}
