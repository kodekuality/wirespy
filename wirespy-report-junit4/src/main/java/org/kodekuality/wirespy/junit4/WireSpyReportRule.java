package org.kodekuality.wirespy.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.kodekuality.wirespy.report.WireSpyMessageCollector;
import org.kodekuality.wirespy.report.WirespyReport;

public class WireSpyReportRule implements TestRule {
    private final WirespyReport report;
    private final WireSpyMessageCollector spyMessageCollector;

    public WireSpyReportRule(WirespyReport report, WireSpyMessageCollector spyMessageCollector) {
        this.report = report;
        this.spyMessageCollector = spyMessageCollector;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                spyMessageCollector.reset();
                base.evaluate();
                report.generate(
                        String.format("%s_%s.html", description.getClassName(), description.getMethodName()),
                        description.getDisplayName(),
                        spyMessageCollector.get()
                );
            }
        };
    }
}
