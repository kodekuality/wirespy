package org.kodekuality.wirespy.proxy.session;

import org.kodekuality.wirespy.proxy.io.CopyRunnable;
import org.kodekuality.wirespy.proxy.io.MultiOutputStream;
import org.kodekuality.wirespy.proxy.request.Destination;
import org.kodekuality.wirespy.proxy.utils.SocketUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ThreadWireSpyProxySession extends Thread implements WireSpyProxySession {
    private final ServerSocket serverSocket;
    private final ServerSocket spyInServerSocket;
    private final ServerSocket spyOutServerSocket;
    private final Destination destination;

    public ThreadWireSpyProxySession(ServerSocket serverSocket, ServerSocket spyInServerSocket, ServerSocket spyOutServerSocket, Destination destination) {
        this.serverSocket = serverSocket;
        this.spyInServerSocket = spyInServerSocket;
        this.spyOutServerSocket = spyOutServerSocket;
        this.destination = destination;
    }

    @Override
    public void run() {
        try {
            do {
                try (
                        Socket spyIn = SocketUtils.accept(spyInServerSocket);
                        Socket spyOut = SocketUtils.accept(spyOutServerSocket);
                        Socket input = SocketUtils.accept(serverSocket);
                        Socket target = SocketUtils.createSocket(destination)
                ) {
                    Thread inboundProcess = new Thread(new CopyRunnable(input.getInputStream(), MultiOutputStream.multi(
                            spyIn.getOutputStream(),
                            target.getOutputStream()
                    )));
                    Thread outboundProcess = new Thread(new CopyRunnable(target.getInputStream(), MultiOutputStream.multi(
                            spyOut.getOutputStream(),
                            input.getOutputStream()
                    )));

                    inboundProcess.start();
                    outboundProcess.start();

                    inboundProcess.join();
                    target.close();
                    outboundProcess.join();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        } catch (Throwable e) {
            // silent
        }
    }

    @Override
    public WireSpyProxySession startProxy() {
        this.start();
        return this;
    }

    @Override
    public WireSpyProxySession stopProxy() {
        SocketUtils.silentlyClose(serverSocket);
        return this;
    }

    @Override
    public WireSpyProxySession awaitTermination() {
        try {
            join();
        } catch (InterruptedException e) {
            // ignore
        }

        return this;
    }
}
