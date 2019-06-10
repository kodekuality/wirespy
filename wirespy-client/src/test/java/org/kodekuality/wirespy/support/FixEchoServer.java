package org.kodekuality.wirespy.support;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import quickfix.*;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class FixEchoServer {
    private final int port;
    private final int fixPort;
    private final AtomicReference<Server> server = new AtomicReference<>();

    public FixEchoServer(int port, int fixPort) {
        this.port = port;
        this.fixPort = fixPort;
    }

    public void start () throws Exception {
        Server server = new Server(port);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    CompletableFuture<Message> completableFuture = new CompletableFuture<>();
                    SessionSettings settings = new SessionSettings(new ByteArrayInputStream(String.format(
                            IOUtils.toString(
                                    getClass().getClassLoader().getResource("initiator.cfg"),
                                    Charset.defaultCharset()
                            ),
                            fixPort
                    ).getBytes()));
                    ThreadedSocketInitiator socketInitiator = new ThreadedSocketInitiator(
                            new FixApp(IOUtils.toString(request.getInputStream(), Charset.defaultCharset()), completableFuture),
                            new MemoryStoreFactory(),
                            settings,
                            new DefaultMessageFactory()
                    );
                    socketInitiator.start();
                    response.getOutputStream().print(completableFuture.get().toString());
                    socketInitiator.stop();
                    response.getOutputStream().close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        server.start();

        this.server.set(server);
    }

    public void stop () throws Exception {
        if (this.server.get() == null) {
            this.server.get().stop();
        }
    }

    public class FixApp extends ApplicationAdapter {
        private final String input;
        private final CompletableFuture<Message> result;

        public FixApp(String input, CompletableFuture<Message> result) {
            this.input = input;
            this.result = result;
        }

        @Override
        public void onLogon(SessionID sessionId) {
            try {
                Message message = new Message(input);
                message.removeField(SenderCompID.FIELD);
                message.removeField(TargetCompID.FIELD);
                Session.sendToTarget(message, sessionId);
            } catch (SessionNotFound | InvalidMessage sessionNotFound) {
                throw new RuntimeException(sessionNotFound);
            }
        }

        @Override
        public void fromApp(Message message, SessionID sessionId) {
            result.complete(message);
        }
    }
}
