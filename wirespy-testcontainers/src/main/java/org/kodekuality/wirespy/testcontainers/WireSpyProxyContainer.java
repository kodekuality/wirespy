package org.kodekuality.wirespy.testcontainers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

public class WireSpyProxyContainer extends GenericContainer<WireSpyProxyContainer> {
    private static final String TARGET_HOST_ENV = "TARGET_HOST";
    private static final String TARGET_PORT_ENV = "TARGET_PORT";

    private static final int INPUT_PORT = 9090;
    private static final int SPY_IN_PORT = 9191;
    private static final int SPY_OUT_PORT = 9192;

    public WireSpyProxyContainer() {
        super("kodekuality/wirespy");

        withExposedPorts(INPUT_PORT, SPY_IN_PORT, SPY_OUT_PORT);
        waitingFor(new LogMessageWaitStrategy().withRegEx(".*Now listening for connections on "+SPY_IN_PORT+".*\n").withTimes(1));
    }

    public WireSpyProxyContainer withTargetHost (String host) {
        return withEnv(TARGET_HOST_ENV, host);
    }

    public WireSpyProxyContainer withTargetPort (int port) {
        return withEnv(TARGET_PORT_ENV, String.valueOf(port));
    }

    public int getInputPort () {
        return getMappedPort(INPUT_PORT);
    }

    public int getSpyInPort () {
        return getMappedPort(SPY_IN_PORT);
    }

    public int getSpyOutPort () {
        return getMappedPort(SPY_OUT_PORT);
    }
}
