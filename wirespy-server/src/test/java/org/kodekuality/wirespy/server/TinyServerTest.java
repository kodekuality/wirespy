package org.kodekuality.wirespy.server;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TinyServerTest {
    public static final IOException IO_EXCEPTION = new IOException();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void rethrowsIOException() throws IOException {
        TinyServer server = new TinyServer() {
            @Override
            public TinyServer start() throws Exception {
                return null;
            }

            @Override
            public TinyServer stop() throws Exception {
                throw IO_EXCEPTION;
            }

            @Override
            public TinyServer join() throws Exception {
                return null;
            }

            @Override
            public int getPort() {
                return 0;
            }
        };

        expectedException.expect(Matchers.is(IO_EXCEPTION));

        server.close();
    }

    @Test
    public void rethrowsOtherCheckedExceptionsIO() throws IOException {
        TinyServer server = new TinyServer() {
            @Override
            public TinyServer start() throws Exception {
                return null;
            }

            @Override
            public TinyServer stop() throws Exception {
                throw new ExecutionException("asad", new IOException());
            }

            @Override
            public TinyServer join() throws Exception {
                return null;
            }

            @Override
            public int getPort() {
                return 0;
            }
        };

        expectedException.expect(IOException.class);

        server.close();
    }

    @Test
    public void rethrowsRuntimeExceptions() throws IOException {
        TinyServer server = new TinyServer() {
            @Override
            public TinyServer start() throws Exception {
                return null;
            }

            @Override
            public TinyServer stop() throws Exception {
                throw new IllegalArgumentException();
            }

            @Override
            public TinyServer join() throws Exception {
                return null;
            }

            @Override
            public int getPort() {
                return 0;
            }
        };

        expectedException.expect(IllegalArgumentException.class);

        server.close();
    }
}