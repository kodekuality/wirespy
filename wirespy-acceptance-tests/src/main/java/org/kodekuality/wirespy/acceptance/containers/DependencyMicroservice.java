package org.kodekuality.wirespy.acceptance.containers;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class DependencyMicroservice extends GenericContainer<DependencyMicroservice> {
    private static final int PORT = 8080;

    public DependencyMicroservice() {
        super("rodolpheche/wiremock");

        withExposedPorts(PORT);
        waitingFor(Wait.forListeningPort());
        withNetworkAliases("dependency");
    }
}