package org.kodekuality.wirespy.support;

import org.kodekuality.wirespy.server.TinyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;

public class EchoServer implements TinyServer {
    public static EchoServer create (int port, Function<Integer, Integer> transform) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        return new EchoServer(
                serverSocket,
                new Thread(() -> {
                    try {
                        Socket accept = serverSocket.accept();

                        int read = accept.getInputStream().read();
                        while (read != -1) {
                            accept.getOutputStream().write(transform.apply(read));
                            read = accept.getInputStream().read();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
        );
    }
    public static EchoServer create (int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        return new EchoServer(
                serverSocket,
                new Thread(() -> {
                    try {
                        Socket accept = serverSocket.accept();

                        int read = accept.getInputStream().read();
                        while (read != -1) {
                            accept.getOutputStream().write(read);
                            read = accept.getInputStream().read();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    private final ServerSocket serverSocket;
    private final Thread thread;

    public EchoServer(ServerSocket serverSocket, Thread thread) {
        this.serverSocket = serverSocket;
        this.thread = thread;
    }

    @Override
    public TinyServer start() throws Exception {
        thread.start();
        return this;
    }

    @Override
    public TinyServer stop() throws Exception {
        serverSocket.close();
        return this;
    }

    @Override
    public TinyServer join() throws Exception {
        thread.join();
        return this;
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }
}
