package org.kodekuality.wirespy.watcher.session;

import org.kodekuality.wirespy.watcher.session.stream.StreamProcessor;

public class ThreadWireSpyWatcherSession implements WireSpyWatcherSession {
    private final StreamProcessor inboundProcessor;
    private final StreamProcessor outboundProcessor;

    public ThreadWireSpyWatcherSession(StreamProcessor inboundProcessor, StreamProcessor outboundProcessor) {
        this.inboundProcessor = inboundProcessor;
        this.outboundProcessor = outboundProcessor;
    }

    @Override
    public WireSpyWatcherSession startSession() {
        inboundProcessor.start();
        outboundProcessor.start();
        return this;
    }

    @Override
    public WireSpyWatcherSession stopSession() {
        inboundProcessor.close();
        outboundProcessor.close();
        return this;
    }

    @Override
    public WireSpyWatcherSession awaitTermination() {
        try {
            inboundProcessor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            outboundProcessor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return this;
    }
}
