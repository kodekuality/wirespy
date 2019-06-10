package org.kodekuality.wirespy.watcher.session.stream;

import org.kodekuality.wirespy.protocol.FrameSequencer;
import org.kodekuality.wirespy.watcher.message.WireSpyMessage;
import org.kodekuality.wirespy.watcher.message.WireSpyMessageReceiver;
import org.kodekuality.wirespy.watcher.utils.SocketUtils;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class StreamProcessor extends Thread {
    private final AtomicReference<Socket> currentSocket = new AtomicReference<>();
    private final AtomicBoolean stop = new AtomicBoolean(false);

    private final String host;
    private final int port;
    private final String originName;
    private final String targetName;
    private final FrameSequencer frameSequencer;
    private final WireSpyMessageReceiver receiver;

    public StreamProcessor(String host, int port, String originName, String targetName, FrameSequencer frameSequencer, WireSpyMessageReceiver receiver) {
        this.host = host;
        this.port = port;
        this.originName = originName;
        this.targetName = targetName;
        this.frameSequencer = frameSequencer;
        this.receiver = receiver;
    }

    @Override
    public void run() {
        try {
            stop.set(false);
            do {
                Socket socket = SocketUtils.createSocket(host, port);
                try {
                    frameSequencer.sequence(socket.getInputStream(), frame -> receiver.handle(new WireSpyMessage(
                            System.nanoTime(),
                            originName,
                            targetName,
                            frame
                    )));
                } catch (IOException ignored) {
                }
            } while (!stop.get());
        } catch (Throwable e) {

        }
    }

    public void close() {
        stop.set(true);
        Optional.ofNullable(currentSocket.get())
                .ifPresent(x -> {
                    try {
                        x.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
