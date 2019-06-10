package org.kodekuality.wirespy.service.tcp;

import org.apache.commons.io.output.TeeOutputStream;
import org.eclipse.jetty.util.BlockingArrayQueue;
import org.kodekuality.wirespy.service.stream.StreamCopier;
import org.kodekuality.wirespy.service.tcp.socket.IOUtils;
import org.kodekuality.wirespy.service.tcp.socket.InitiatorSocketFactory;
import org.kodekuality.wirespy.service.tcp.socket.ListenerSocketFactory;
import org.kodekuality.wirespy.service.tcp.socket.SocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpyTcpProxyService implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(SpyTcpProxyService.class);
    private final List<ServerSocket> serverSockets = new CopyOnWriteArrayList<>();
    private final ExecutorService executorService = new ThreadPoolExecutor(10, 100, 1, TimeUnit.SECONDS, new BlockingArrayQueue<>());

    public SpyTcpSession startSpySession(SpySource spySource, SpyTarget spyTarget) throws IOException {
        ServerSocket inStreamServerSocket = new ServerSocket(0);
        ServerSocket outStreamServerSocket = new ServerSocket(0);
        ServerSocket sourceServerSocket = new ServerSocket(spySource.getPort());

        serverSockets.add(inStreamServerSocket);
        serverSockets.add(outStreamServerSocket);
        serverSockets.add(sourceServerSocket);

        SocketFactory inStreamSocketFactory = new ListenerSocketFactory(spySource.getName(), inStreamServerSocket);
        SocketFactory outStreamSocketFactory = new ListenerSocketFactory(spySource.getName(), outStreamServerSocket);
        SocketFactory sourceSocketFactory = new ListenerSocketFactory(spySource.getName(), sourceServerSocket);
        SocketFactory targetSocketFactory = new InitiatorSocketFactory(spyTarget.getName(), spyTarget.getHost(), spyTarget.getPort());

        executorService.execute(() -> {
            try {
                while (true) {
                    Socket inStream = inStreamSocketFactory.create();
                    Socket outStream = outStreamSocketFactory.create();
                    Socket source = sourceSocketFactory.create();

                    executorService.execute(() -> {
                        Socket target = targetSocketFactory.create();

                        logger.info("Proxy from {} -> {} established", spySource.getName(), spyTarget.getName());

                        try {
                            StreamCopier forward = new StreamCopier("Forward", source.getInputStream(), new TeeOutputStream(
                                    target.getOutputStream(),
                                    inStream.getOutputStream()
                            ));
                            StreamCopier backward = new StreamCopier("Forward", target.getInputStream(), new TeeOutputStream(
                                    source.getOutputStream(),
                                    outStream.getOutputStream()
                            ));

                            Thread forwardThread = new Thread(forward);
                            forwardThread.start();

                            Thread backwardThread = new Thread(backward);
                            backwardThread.start();

                            logger.info("Waiting for copier {} ({}) -> {} ({}) to finish", spySource.getName(), spySource.getPort(), spyTarget.getName(), spyTarget.getPort());
                            forwardThread.join();
                            logger.info("Copier {} ({}) -> {} ({}) finished", spySource.getName(), spySource.getPort(), spyTarget.getName(), spyTarget.getPort());
                            logger.info("Waiting for copier {} ({}) -> {} ({}) to finish", spyTarget.getName(), spyTarget.getPort(), spySource.getName(), spySource.getPort());
                            backwardThread.join();
                            logger.info("Copier {} ({}) -> {} ({}) finished", spyTarget.getName(), spyTarget.getPort(), spySource.getName(), spySource.getPort());
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            } finally {
                closeAndRemove(inStreamServerSocket);
                closeAndRemove(outStreamServerSocket);
                closeAndRemove(sourceServerSocket);
            }
        });

        return new SpyTcpSession(
                inStreamSocketFactory.getPort(),
                outStreamSocketFactory.getPort()
        );
    }

    private void closeAndRemove (ServerSocket serverSocket) {
        IOUtils.silentlyCloseCloseable(serverSocket);
        serverSockets.remove(serverSocket);
    }

    @Override
    public void close() {
        for (ServerSocket serverSocket : serverSockets) {
            IOUtils.silentlyCloseCloseable(serverSocket);
        }
        serverSockets.clear();
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
