package org.kodekuality.wirespy.acceptance.containers;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

public class OrchestratorMicroservice extends GenericContainer<OrchestratorMicroservice> {
    private static final int PORT = 80;

    public OrchestratorMicroservice() {
        super("diouxx/apache-proxy");

        withExposedPorts(PORT);
        waitingFor(Wait.forListeningPort());
        withCopyFileToContainer(MountableFile.forHostPath("src/main/resources/apache.cfg"), "/opt/proxy-conf/proxy.conf");
        withNetworkAliases("orchestrator");
    }
}
