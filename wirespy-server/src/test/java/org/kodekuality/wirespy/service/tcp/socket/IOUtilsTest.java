package org.kodekuality.wirespy.service.tcp.socket;

import org.junit.Test;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IOUtilsTest {
    @Test
    public void flushIgnoreIfExceptionThrownWhenGettingOutputStream() throws IOException {
        Socket socket = mock(Socket.class);

        when(socket.getOutputStream()).thenThrow(IOException.class);

        IOUtils.silentlyFlush(socket);
    }

    @Test
    public void flushIgnoreIfExceptionThrownWhenFlushing() throws IOException {
        Socket socket = mock(Socket.class);
        OutputStream outputStream = mock(OutputStream.class);

        when(socket.getOutputStream()).thenReturn(outputStream);
        doThrow(IOException.class).when(outputStream).flush();

        IOUtils.silentlyFlush(socket);
    }

    @Test
    public void closeSocketIgnoreException() throws IOException {
        Socket socket = mock(Socket.class);

        doThrow(IOException.class).when(socket).close();

        IOUtils.silentlyClose(socket);
    }

    @Test
    public void closeCloseableIgnoreException() throws IOException {
        Closeable closeable = mock(Closeable.class);

        doThrow(IOException.class).when(closeable).close();

        IOUtils.silentlyCloseCloseable(closeable);
    }
}