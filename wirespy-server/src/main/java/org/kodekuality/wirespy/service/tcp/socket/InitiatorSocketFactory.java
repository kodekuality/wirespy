package org.kodekuality.wirespy.service.tcp.socket;

import org.kodekuality.wirespy.service.concurrency.ConcurrencyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

public class InitiatorSocketFactory implements SocketFactory {
    private static final Logger logger = LoggerFactory.getLogger(InitiatorSocketFactory.class);
    private static final int WAIT = 1;
    private static final TimeUnit UNIT = TimeUnit.SECONDS;

    private final List<CompletableFuture<Socket>> createdSockets = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final String name;
    private final String host;
    private final int port;

    public InitiatorSocketFactory(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public Socket create() {
        CompletableFuture<Socket> socketCompletableFuture = new CompletableFuture<>();
        executorService.execute(() -> {
            do {
                try {
                    Socket socket = new Socket(host, port);
                    socketCompletableFuture.complete(socket);
                    return;
                } catch (IOException e) {
                    logger.debug("Could not connect to {} ({}:{}), retrying in {} {}", name, host, port, WAIT, UNIT, e);
                    ConcurrencyUtils.sleepSilently(UNIT.toMillis(WAIT));
                }
            } while (true);
        });
        createdSockets.add(socketCompletableFuture);
        Socket socket = ConcurrencyUtils.getOrRethrow(socketCompletableFuture);
        logger.info("Connection initiated with {} on {}:{}", name, host, port);
        return socket;
    }

    @Override
    public void close() {
        Collection<CompletableFuture<Socket>> sockets = Collections.unmodifiableCollection(createdSockets);
        for (CompletableFuture<Socket> createdSocket : sockets) {
            Optional.ofNullable(createdSocket.getNow(null))
                    .ifPresent(x -> {
                        IOUtils.silentlyFlush(x);
                        IOUtils.silentlyClose(x);
                    });
            createdSockets.remove(createdSocket);
        }
    }
}
