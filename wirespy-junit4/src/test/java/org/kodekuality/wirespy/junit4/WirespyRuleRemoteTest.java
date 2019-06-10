package org.kodekuality.wirespy.junit4;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.kodekuality.wirespy.WirespyClient;
import org.kodekuality.wirespy.WirespyServer;
import org.kodekuality.wirespy.protocol.http.HttpProtocol;
import org.kodekuality.wirespy.report.WirespyReport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class WirespyRuleRemoteTest {
    private static final WirespyServer wirespyServer = WirespyServer.wirespyServer(8080);
    private static final WirespyClient wirespyClient = WirespyClient.wirespyClient(8080);
    private static final Server server = new Server(9092);

    @BeforeClass
    public static void setup () throws Exception {
        wirespyServer.start();
        wirespyClient.from("A", 9091).to("B", 9092).as(HttpProtocol.http());

        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write("test".getBytes());
                response.setStatus(200);
                response.getOutputStream().close();
            }
        });
        server.start();
    }

    @AfterClass
    public static void tearDown () throws Exception {
        wirespyServer.stop();
        wirespyClient.close();
        server.stop();
    }

    @Rule
    public WirespyRemoteRule wirespyRule = WirespyRemoteRule.wirespyRemote(wirespyClient)
            .withReport(WirespyReport.wirespyReport(new File("build/wirespy-report")));

    @Test
    public void remoteTest1() throws Exception {
        HttpResponse<String> response = Unirest.get("http://localhost:9091/test").asString();

        assertThat(response.getBody(), CoreMatchers.is("test"));
    }

    @Test
    public void remoteTest2() throws Exception {
        HttpResponse<String> response = Unirest.get("http://localhost:9091/testa").asString();

        assertThat(response.getBody(), CoreMatchers.is("test"));
    }
}