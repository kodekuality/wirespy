package org.kodekuality.wirespy.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kodekuality.wirespy.WirespyClient;
import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.report.Request;
import org.kodekuality.wirespy.report.WirespyReport;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WirespyRemoteRule implements TestRule {
    public static WirespyRemoteRule wirespyRemote(WirespyClient client) {
        try {
            return new WirespyRemoteRule(client);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final WirespyClient client;
    private final AtomicReference<WirespyReport> report = new AtomicReference<>();
    private final AtomicReference<Predicate<Message>> filter = new AtomicReference<>();

    public WirespyRemoteRule(WirespyClient client) {
        this.client = client;
    }

    public WirespyRemoteRule withReport(WirespyReport report) {
        this.report.set(report);
        return this;
    }

    public WirespyRemoteRule withMessageFilter (Predicate<Message> predicate) {
        this.filter.set(predicate);
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                client.reset();
                base.evaluate();

                Optional.ofNullable(report.get())
                        .ifPresent(x -> {
                            try {
                                x.generate(String.format("%s.html", description.getMethodName()), new Request(
                                        description.getDisplayName(),
                                        client.allTraffic().stream()
                                                .filter(m -> Optional.ofNullable(filter.get()).orElse(y -> true).test(m))
                                        .collect(Collectors.toList())
                                ));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
            }
        };
    }
}
