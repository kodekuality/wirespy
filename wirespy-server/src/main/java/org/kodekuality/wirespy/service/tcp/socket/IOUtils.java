package org.kodekuality.wirespy.service.tcp.socket;

import java.io.Closeable;
import java.io.OutputStream;
import java.net.Socket;

public class IOUtils {
    private IOUtils () {}
    public static void silentlyFlush (Socket socket) {
        try {
            silentlyFlush(socket.getOutputStream());
        } catch (Throwable e) {
            // ignored
        }
    }

    public static void silentlyFlush (OutputStream outputStream) {
        try {
            outputStream.flush();
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
