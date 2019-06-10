package org.kodekuality.wirespy.support;

import org.apache.commons.io.IOUtils;
import quickfix.*;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class FixServer {
    private final int port;
    private AtomicReference<SocketAcceptor> socketAcceptor = new AtomicReference<>();

    public FixServer(int port) {
        this.port = port;
    }

    public void start () {
        try {
            SessionSettings settings = new SessionSettings(new ByteArrayInputStream(String.format(
                    IOUtils.toString(
                            getClass().getClassLoader().getResource("acceptor.cfg"),
                            Charset.defaultCharset()
                    ),
                    port
            ).getBytes()));
            socketAcceptor.set(new SocketAcceptor(
                    new EchoFixApp(),
                    new MemoryStoreFactory(),
                    settings,
                    new DefaultMessageFactory()
            ));
            socketAcceptor.get().start();
        } catch (ConfigError | IOException configError) {
            throw new RuntimeException(configError);
        }
    }

    public void stop () {
        this.socketAcceptor.get().stop(true);
    }

    public static class EchoFixApp extends ApplicationAdapter {
        private final AtomicBoolean sent = new AtomicBoolean(false);
        @Override
        public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
            if (sent.compareAndSet(false, true)) {
                try {
                    message.removeField(SenderCompID.FIELD);
                    message.removeField(TargetCompID.FIELD);
                    Session.sendToTarget(message, sessionId);
                } catch (SessionNotFound sessionNotFound) {
                    throw new RuntimeException(sessionNotFound);
                }
            }
        }
    }
}
