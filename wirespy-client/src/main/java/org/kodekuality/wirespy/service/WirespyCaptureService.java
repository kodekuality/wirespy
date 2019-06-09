package org.kodekuality.wirespy.service;

import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.protocol.FrameSequencer;

import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public class WirespyCaptureService {
    private final Consumer<Message> messageConsumer;

    public WirespyCaptureService(Consumer<Message> messageConsumer) {
        this.messageConsumer = messageConsumer;
    }

    public void create(CaptureStream sent, CaptureStream received) {
        Thread sentThread = new Thread(new CaptureStreamRunner(
                sent.getName(),
                received.getName(),
                sent.getSocket(),
                sent.getSequencer(),
                messageConsumer
        ), String.format("from-%s-to-%s-%d", sent.getName(), received.getName(), sent.getSocket().getLocalPort()));
        sentThread.setDaemon(true);
        sentThread.start();
        Thread receiveThread = new Thread(new CaptureStreamRunner(
                received.getName(),
                sent.getName(),
                received.getSocket(),
                received.getSequencer(),
                messageConsumer
        ), String.format("from-%s-to-%s-%d", received.getName(), sent.getName(), received.getSocket().getLocalPort()));
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    public static class CaptureStream {
        private final String name;
        private final Socket socket;
        private final FrameSequencer sequencer;

        public CaptureStream(String name, Socket socket, FrameSequencer sequencer) {
            this.name = name;
            this.socket = socket;
            this.sequencer = sequencer;
        }

        public String getName() {
            return name;
        }

        public Socket getSocket() {
            return socket;
        }

        public FrameSequencer getSequencer() {
            return sequencer;
        }
    }

    private static class CaptureStreamRunner implements Runnable {
        private final String from;
        private final String to;
        private final Socket socket;
        private final FrameSequencer sequencer;
        private final Consumer<Message> messageConsumer;

        public CaptureStreamRunner(String from, String to, Socket socket, FrameSequencer sequencer, Consumer<Message> messageConsumer) {
            this.from = from;
            this.to = to;
            this.socket = socket;
            this.sequencer = sequencer;
            this.messageConsumer = messageConsumer;
        }

        @Override
        public void run() {
            try {
                sequencer.sequence(socket.getInputStream(), frame -> messageConsumer.accept(new Message(
                        System.nanoTime(),
                        from,
                        to,
                        frame
                )));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
