package org.kodekuality.wirespy.service.stream;

import java.io.InputStream;
import java.io.OutputStream;

public class StreamCopierFactory {
    public StreamCopier create (String inputName, InputStream inputStream, OutputStream outputStream) {
        return new StreamCopier(inputName, inputStream, outputStream);
    }
}
