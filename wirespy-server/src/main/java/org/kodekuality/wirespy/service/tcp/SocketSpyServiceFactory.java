package org.kodekuality.wirespy.service.tcp;

import org.kodekuality.wirespy.service.stream.StreamCopierFactory;

public class SocketSpyServiceFactory {
    private final StreamCopierFactory streamCopierFactory;

    public SocketSpyServiceFactory(StreamCopierFactory streamCopierFactory) {
        this.streamCopierFactory = streamCopierFactory;
    }

    public SocketSpyService create () {
        return new SocketSpyService(
                streamCopierFactory
        );
    }
}
