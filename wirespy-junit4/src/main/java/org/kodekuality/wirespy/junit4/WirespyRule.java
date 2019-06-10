package org.kodekuality.wirespy.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kodekuality.wirespy.WirespyClient;
import org.kodekuality.wirespy.WirespyServer;
import org.kodekuality.wirespy.report.Request;
import org.kodekuality.wirespy.report.WirespyReport;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class WirespyRule implements TestRule {
    public static WirespyRule wirespy (int port) {
        try {
            if (!isListening(port)) {
                return new WirespyRule(
                        Optional.of(WirespyServer.wirespyServer(port)),
                        WirespyClient.wirespyClient(port)
                );
            } else {
                return new WirespyRule(
                        Optional.empty(),
                        WirespyClient.wirespyClient(port)
                );
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isListening(int port) {
        try {
            try (Socket socket = new Socket("localhost", port)) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }

    private Optional<WirespyServer> server;
    private final AtomicReference<Consumer<WirespyClient>> clientConfigure = new AtomicReference<>();
    private final WirespyClient client;
    private final AtomicReference<WirespyReport> report = new AtomicReference<>();

    public WirespyRule(Optional<WirespyServer> server, WirespyClient client) {
        this.server = server;
        this.client = client;
    }

    public WirespyRule withReport (WirespyReport report) {
        this.report.set(report);
        return this;
    }

    public WirespyRule client (Consumer<WirespyClient> consumer) {
        clientConfigure.set(consumer);
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                server.ifPresent(x -> {
                    try {
                        x.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                try {
                    client.reset();
                    Optional.ofNullable(clientConfigure.get())
                            .ifPresent(x -> x.accept(client));

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
                    server.ifPresent(x -> {
                        try {
                            x.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    client.stop();
                }
            }
        };
    }
}
