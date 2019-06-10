package org.kodekuality.wirespy.acceptance;

import org.kodekuality.wirespy.acceptance.containers.DependencyMicroservice;
import org.kodekuality.wirespy.acceptance.containers.OrchestratorMicroservice;
import org.kodekuality.wirespy.acceptance.containers.WirespyContainer;
import org.testcontainers.containers.Network;

public class TestEnvironment {
    private final OrchestratorMicroservice orchestratorMicroservice = new OrchestratorMicroservice();
    private final DependencyMicroservice dependencyMicroservice = new DependencyMicroservice();
    private final WirespyContainer userToOrch = new WirespyContainer().withNetworkAliases("spy-orchestrator")
            .withEnv("TARGET_HOST", "orchestrator")
            .withEnv("TARGET_PORT", "80");
    private final WirespyContainer orchToDep = new WirespyContainer().withNetworkAliases("spy-dependency")
            .withEnv("TARGET_HOST", "dependency")
            .withEnv("TARGET_PORT", "8080");

    public void start () {
        final Network network = Network.newNetwork();

        // same network
        orchestratorMicroservice.withNetwork(network);
        dependencyMicroservice.withNetwork(network);
        userToOrch.withNetwork(network);
        orchToDep.withNetwork(network);

        // start
        dependencyMicroservice.start();
        orchToDep.start();
        orchestratorMicroservice.start();
        userToOrch.start();
    }

    public void stop() {
        orchestratorMicroservice.stop();
        dependencyMicroservice.stop();
        userToOrch.stop();
        orchToDep.stop();
    }

    public int getOrchestratorPort() {
        return orchestratorMicroservice.getMappedPort(80);
    }

    public int getDependencyPort() {
        return dependencyMicroservice.getMappedPort(8080);
    }

    public WirespyContainer getUserToOrchestrator() {
        return userToOrch;
    }

    public WirespyContainer getOrchestratorToDependency() {
        return orchToDep;
    }
}
