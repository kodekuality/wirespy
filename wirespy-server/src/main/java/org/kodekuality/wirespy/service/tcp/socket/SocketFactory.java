package org.kodekuality.wirespy.service.tcp.socket;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public interface SocketFactory extends Closeable {
    Socket create () throws IOException;
}
