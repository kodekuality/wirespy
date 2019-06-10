package org.kodekuality.wirespy.proxy.session;

import java.io.Closeable;
import java.io.IOException;

public interface WireSpyProxySession extends Closeable {
    WireSpyProxySession startProxy ();
    WireSpyProxySession stopProxy ();
    WireSpyProxySession awaitTermination ();

    @Override
    default void close() throws IOException {
        stopProxy();
    }
}
