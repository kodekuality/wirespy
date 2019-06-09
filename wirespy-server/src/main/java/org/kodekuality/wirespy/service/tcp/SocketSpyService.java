package org.kodekuality.wirespy.service.tcp;

import org.apache.commons.io.output.TeeOutputStream;
import org.kodekuality.wirespy.service.concurrency.ConcurrencyUtils;
import org.kodekuality.wirespy.service.stream.StreamCopierFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SocketSpyService {
    private static final Logger logger = LoggerFactory.getLogger(SocketSpyService.class);

    private final ExecutorService executorService  = Executors.newFixedThreadPool(2);
    private final StreamCopierFactory streamCopierFactory;

    public SocketSpyService(StreamCopierFactory streamCopierFactory) {
        this.streamCopierFactory = streamCopierFactory;
    }

    public void spy(SpySocketFactory sourceSocket, SpySocketFactory targetSocket, Socket inStream, Socket outStream) {
        try (Socket source = sourceSocket.get()) {
            try (Socket target = targetSocket.get()) {
                try {
                    // send data to destination and also spy socket
                    Future<Boolean> sendFuture = executorService.submit(
                            streamCopierFactory.create(
                                    sourceSocket.getName(),
                                    source.getInputStream(),
                                    new TeeOutputStream(target.getOutputStream(), inStream.getOutputStream())
                            )
                    );

                    try {
                        // send data to source and also spy socket
                        Future<Boolean> receiveFuture = executorService.submit(
                                streamCopierFactory.create(
                                        targetSocket.getName(),
                                        target.getInputStream(),
                                        new TeeOutputStream(source.getOutputStream(), outStream.getOutputStream())
                                )
                        );

                        ConcurrencyUtils.waitSilently(sendFuture);
                        ConcurrencyUtils.waitSilently(receiveFuture);
                    } finally {
                        sendFuture.cancel(false);
                    }
                } catch (Exception e) {
                    logger.debug("Stopped communications channel between {} (port: {}) and {} (port: {})",
                            sourceSocket.getName(),
                            sourceSocket.getPort(),
                            targetSocket.getName(),
                            targetSocket.getPort(),
                            e);
                }
            } catch (Exception e) {
                logger.info("Could not reach {} (port: {})",
                        targetSocket.getName(),
                        targetSocket.getPort(),
                        e);
            }
        } catch (Exception e) {
            logger.info("Could not start listening on {} (port: {})",
                    sourceSocket.getName(),
                    sourceSocket.getPort(),
                    e);
        }
    }

}
