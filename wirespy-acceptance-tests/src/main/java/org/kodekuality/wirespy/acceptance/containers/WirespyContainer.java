package org.kodekuality.wirespy.acceptance.containers;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Paths;

public class WirespyContainer extends GenericContainer<WirespyContainer> {
    private static final int INPUT = 9090;
    private static final int INBOUND_SPY_PORT = 9191;
    private static final int OUTBOUD_SPY_PORT = 9192;

    public WirespyContainer() {
        super(
                new ImageFromDockerfile("wirespy", false)
                        .withFileFromPath("wirespy-app-SNAPSHOT-all.jar", Paths.get("../wirespy-app/build/libs/wirespy-app-SNAPSHOT-all.jar"))
                        .withFileFromPath("Dockerfile", Paths.get("src/main/resources/Dockerfile"))
        );

        withExposedPorts(INPUT, INBOUND_SPY_PORT, OUTBOUD_SPY_PORT);
        withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger(WirespyContainer.class)));
        waitingFor(new LogMessageWaitStrategy().withRegEx(".*Now listening for connections on 9191.*\n").withTimes(1));
    }

    @Override
    public void start() {
        super.start();
    }

    public int getInSpyPort () {
        return getMappedPort(INBOUND_SPY_PORT);
    }

    public int getOutSpyPort () {
        return getMappedPort(OUTBOUD_SPY_PORT);
    }

    public int getEntryPort() {
        return getMappedPort(INPUT);
    }
}
