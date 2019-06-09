package org.kodekuality.wirespy.service.tcp;

import org.junit.Test;
import org.kodekuality.wirespy.service.stream.StreamCopierFactory;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SocketSpyServiceTest {
    private final StreamCopierFactory streamCopierFactory = mock(StreamCopierFactory.class);
    private SocketSpyService underTest = new SocketSpyService(streamCopierFactory);

    @Test
    public void exceptionGettingInSocket() throws Exception {
        SpySocketFactory sourceSocket = mock(SpySocketFactory.class);
        SpySocketFactory targetSocket = mock(SpySocketFactory.class);

        when(sourceSocket.get()).thenThrow(IOException.class);

        underTest.spy(sourceSocket, targetSocket, mock(Socket.class), mock(Socket.class));
    }

    @Test
    public void exceptionGettingOutSocket() throws Exception {
        SpySocketFactory sourceSocket = mock(SpySocketFactory.class);
        SpySocketFactory targetSocket = mock(SpySocketFactory.class);

        Socket inSocket = mock(Socket.class);

        when(sourceSocket.get()).thenReturn(inSocket);
        when(targetSocket.get()).thenThrow(IOException.class);

        underTest.spy(sourceSocket, targetSocket, mock(Socket.class), mock(Socket.class));
    }
}