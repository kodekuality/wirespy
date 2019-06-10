package org.kodekuality.wirespy.acceptance;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.*;
import org.kodekuality.wirespy.protocol.http.HttpProtocol;
import org.kodekuality.wirespy.report.Request;
import org.kodekuality.wirespy.report.WirespyReport;
import org.kodekuality.wirespy.watcher.WireSpyWatcher;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class ExampleTest {
    @Test
    public void test1() throws UnirestException, IOException {
        TestEnvironment testEnvironment = new TestEnvironment();
        testEnvironment.start();

        WireMock.configureFor(testEnvironment.getDependencyPort());
        WireMock.stubFor(WireMock.get("/").willReturn(aResponse().withBody("hello")));

        WirespyReport wirespyReport = WirespyReport.wirespyReport(new File("reports"));
        WireSpyWatcher.newWatcher()
                .watch("localhost")
                .from("UI", testEnvironment.getUserToOrchestrator().getInSpyPort())
                .to("Backend", testEnvironment.getUserToOrchestrator().getOutSpyPort())
                .as(HttpProtocol.http())
                .publishTo(wirespyReport)
                .watch("localhost")
                .from("Backend", testEnvironment.getOrchestratorToDependency().getInSpyPort())
                .to("Dependency", testEnvironment.getOrchestratorToDependency().getOutSpyPort())
                .as(HttpProtocol.http())
                .publishTo(wirespyReport);

        try {
            HttpResponse<String> result1 = Unirest
                    .get(String.format("http://localhost:%d/", testEnvironment.getUserToOrchestrator().getEntryPort()))
                    .asString();
            System.out.println(result1.getBody());
        } finally {
            wirespyReport.generate("test1.html", "Scenario");
        }

        testEnvironment.stop();
    }
}