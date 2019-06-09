package org.kodekuality.wirespy.service.tcp.socket;

import java.io.Closeable;
import java.net.Socket;

public class IOUtils {
    private IOUtils () {}
    public static void silentlyFlush (Socket socket) {
        try {
            socket.getOutputStream().flush();
        } catch (Throwable e) {
            // ignored
        }
    }

    public static void silentlyClose (Socket socket) {
        try {
            socket.close();
        } catch (Throwable e) {
            // ignored
        }
    }

    public static void silentlyCloseCloseable (Closeable closeable) {
        try {
            closeable.close();
        } catch (Throwable e) {
            // ignored
        }
    }
}
