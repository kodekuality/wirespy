package org.kodekuality.wirespy.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kodekuality.wirespy.WirespyClient;
import org.kodekuality.wirespy.WirespyServer;
import org.kodekuality.wirespy.report.Request;
import org.kodekuality.wirespy.report.WirespyReport;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WirespyRule implements TestRule {
    public static WirespyRule wirespy(int port) {
        try {
            return new WirespyRule(
                    WirespyServer.wirespyServer(port),
                    WirespyClient.wirespyClient(port)
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final WirespyServer server;
    private final WirespyClient client;
    private final AtomicReference<Consumer<WirespyClient>> configure = new AtomicReference<>();
    private final AtomicReference<WirespyReport> report = new AtomicReference<>();

    public WirespyRule(WirespyServer server, WirespyClient client) {
        this.server = server;
        this.client = client;
    }

    public WirespyRule withReport(WirespyReport report) {
        this.report.set(report);
        return this;
    }

    public WirespyRule configure (Consumer<WirespyClient> client) {
        this.configure.set(client);
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    server.start();
                    client.reset();
                    Optional.ofNullable(configure.get()).ifPresent(x -> x.accept(client));
                    base.evaluate();

                    Optional.ofNullable(report.get())
                            .ifPresent(x -> {
                                try {
                                    x.generate(String.format("%s.html", description.getMethodName()), new Request(
                                            description.getDisplayName(),
                                            client.allTraffic()
                                    ));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                } finally {
                    server.stop();
                    client.close();
                }
            }
        };
    }
}
