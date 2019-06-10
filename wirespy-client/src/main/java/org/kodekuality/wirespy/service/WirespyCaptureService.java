package org.kodekuality.wirespy.service;

import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.protocol.FrameSequencer;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WirespyCaptureService implements Closeable {
    private final Consumer<Message> messageConsumer;
    private final List<Closeable> closeables = new CopyOnWriteArrayList<>();

    public WirespyCaptureService(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void create(CaptureStream sent, CaptureStream received) {
        process(sent, received);
        process(received, sent);
    }

    private void process(CaptureStream sent, CaptureStream received) {
        CaptureStreamRunner inboundStreamRunner = new CaptureStreamRunner(
                sent.getName(),
                received.getName(),
                sent.getSocketSupplier(),
                sent.getSequencer(),
                messageConsumer
        );
        closeables.add(inboundStreamRunner);

        Thread sentThread = new Thread(inboundStreamRunner, String.format("from-%s-to-%s", sent.getName(), received.getName()));
        sentThread.setDaemon(true);
        sentThread.start();
    }

    @Override
    public void close() {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (Exception e) {

            }
        }
        closeables.clear();
    }

    public static class CaptureStream {
        private final String name;
        private final SocketFactory socketSupplier;
        private final FrameSequencer sequencer;

        public CaptureStream(String name, SocketFactory socketSupplier, FrameSequencer sequencer) {
            this.name = name;
            this.socketSupplier = socketSupplier;
            this.sequencer = sequencer;
        }

        public String getName() {
            return name;
        }

        public SocketFactory getSocketSupplier() {
            return socketSupplier;
        }

        public FrameSequencer getSequencer() {
            return sequencer;
        }
    }

    private static class CaptureStreamRunner implements Runnable, Closeable {
        private final String from;
        private final String to;
        private final SocketFactory socketFactory;
        private final FrameSequencer sequencer;
        private final Consumer<Message> messageConsumer;
        private final AtomicReference<Socket> currentSocket = new AtomicReference<>();

        public CaptureStreamRunner(String from, String to, SocketFactory socketSupplier, FrameSequencer sequencer, Consumer<Message> messageConsumer) {
            this.from = from;
            this.to = to;
            this.socketFactory = socketSupplier;
            this.sequencer = sequencer;
            this.messageConsumer = messageConsumer;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try (Socket socket = currentSocket.updateAndGet(x -> socketFactory.create())) {
                        sequencer.sequence(socket.getInputStream(), frame -> messageConsumer.accept(new Message(
                                System.nanoTime(),
                                from,
                                to,
                                frame
                        )));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable e) {

            }
        }

        @Override
        public void close() throws IOException {
            Optional.ofNullable(currentSocket.get())
                    .ifPresent(x -> {
                        try {
                            x.close();
                        } catch (IOException e) {

                        }
                    });
        }
    }
}
