package org.kodekuality.wirespy.watcher;

import org.kodekuality.wirespy.watcher.config.Configuration;
import org.kodekuality.wirespy.watcher.config.Spy;
import org.kodekuality.wirespy.protocol.Protocol;
import org.kodekuality.wirespy.watcher.message.WireSpyMessageReceiver;
import org.kodekuality.wirespy.watcher.session.WireSpyWatcherSession;
import org.kodekuality.wirespy.watcher.session.WireSpyWatcherSessionFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WireSpyWatcher implements Closeable {
    private static final WireSpyWatcherSessionFactory FACTORY = new WireSpyWatcherSessionFactory();
    private final List<WireSpyWatcherSession> activeSessions = new CopyOnWriteArrayList<>();

    public static WireSpyWatcher newWatcher () {
        return new WireSpyWatcher();
    }

    public FromBuilder watch (String host) {
        return new FromBuilder(host);
    }

    @Override
    public void close() throws IOException {
        for (WireSpyWatcherSession activeSession : activeSessions) {
            activeSession.stopSession();
        }
        activeSessions.clear();
    }

    public class FromBuilder {
        private final String host;

        public FromBuilder(String host) {
            this.host = host;
        }

        public ToBuilder from (String originName, int spyPort) {
            return new ToBuilder(host, originName, spyPort);
        }
    }

    public class ToBuilder {
        private final String host;
        private final String originName;
        private final int inPort;

        public ToBuilder(String host, String originName, int inPort) {
            this.host = host;
            this.originName = originName;
            this.inPort = inPort;
        }

        public ProtocolBuilder to (String targetName, int outPort) {
            return new ProtocolBuilder(
                    host,
                    originName,
                    inPort,
                    targetName,
                    outPort
            );
        }
    }

    public class ProtocolBuilder {
        private final String host;
        private final String originName;
        private final int inPort;
        private final String targetName;
        private final int outPort;

        public ProtocolBuilder(String host, String originName, int inPort, String targetName, int outPort) {
            this.host = host;
            this.originName = originName;
            this.inPort = inPort;
            this.targetName = targetName;
            this.outPort = outPort;
        }

        public PublishBuilder as (Protocol protocol) {
            return new PublishBuilder(
                    host,
                    originName,
                    inPort,
                    targetName,
                    outPort,
                    protocol
            );
        }
    }

    public class PublishBuilder {
        private final String host;
        private final String originName;
        private final int inPort;
        private final String targetName;
        private final int outPort;
        private final Protocol protocol;

        public PublishBuilder(String host, String originName, int inPort, String targetName, int outPort, Protocol protocol) {
            this.host = host;
            this.originName = originName;
            this.inPort = inPort;
            this.targetName = targetName;
            this.outPort = outPort;
            this.protocol = protocol;
        }

        public WireSpyWatcher publishTo (WireSpyMessageReceiver receiver) {
            WireSpyWatcherSession session = FACTORY.create(new Configuration(
                    new Spy(
                            host,
                            inPort,
                            outPort
                    ),
                    originName,
                    targetName,
                    protocol,
                    receiver
            ));
            session.startSession();
            activeSessions.add(session);
            return WireSpyWatcher.this;
        }
    }
}
