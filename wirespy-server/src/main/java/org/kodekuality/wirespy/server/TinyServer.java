package org.kodekuality.wirespy.server;

import java.io.Closeable;
import java.io.IOException;

public interface TinyServer extends Closeable {
    TinyServer start () throws Exception;
    TinyServer stop () throws Exception;
    TinyServer join () throws Exception;
    int getPort ();

    @Override
    default void close() throws IOException {
        try {
            stop();
        } catch (IOException | RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}
