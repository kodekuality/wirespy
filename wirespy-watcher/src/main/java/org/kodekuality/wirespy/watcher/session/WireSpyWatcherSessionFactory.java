package org.kodekuality.wirespy.watcher.session;

import org.kodekuality.wirespy.watcher.config.Configuration;
import org.kodekuality.wirespy.watcher.session.stream.StreamProcessor;

public class WireSpyWatcherSessionFactory {
    public WireSpyWatcherSession create (Configuration configuration) {
        return new ThreadWireSpyWatcherSession(
                new StreamProcessor(
                        configuration.getSpy().getHost(),
                        configuration.getSpy().getInboundPort(),
                        configuration.getOriginName(),
                        configuration.getTargetName(),
                        configuration.getProtocol().getSentDataSequencer(),
                        configuration.getReceiver()
                ),
                new StreamProcessor(
                        configuration.getSpy().getHost(),
                        configuration.getSpy().getOutboundPort(),
                        configuration.getTargetName(),
                        configuration.getOriginName(),
                        configuration.getProtocol().getReceivedDataSequencer(),
                        configuration.getReceiver()
                )
        );
    }
}
