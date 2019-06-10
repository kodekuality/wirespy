package org.kodekuality.wirespy.watcher.config;

import org.kodekuality.wirespy.protocol.Protocol;
import org.kodekuality.wirespy.watcher.message.WireSpyMessageReceiver;

public class Configuration {
    private final Spy spy;
    private final String originName;
    private final String targetName;
    private final Protocol protocol;
    private final WireSpyMessageReceiver receiver;

    public Configuration(Spy spy, String originName, String targetName, Protocol protocol, WireSpyMessageReceiver receiver) {
        this.spy = spy;
        this.originName = originName;
        this.targetName = targetName;
        this.protocol = protocol;
        this.receiver = receiver;
    }

    public Spy getSpy() {
        return spy;
    }

    public String getOriginName() {
        return originName;
    }

    public String getTargetName() {
        return targetName;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public WireSpyMessageReceiver getReceiver() {
        return receiver;
    }
}
