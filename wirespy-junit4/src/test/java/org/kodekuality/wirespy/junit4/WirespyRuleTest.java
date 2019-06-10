package org.kodekuality.wirespy.junit4;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.kodekuality.wirespy.protocol.http.HttpProtocol;
import org.kodekuality.wirespy.report.WirespyReport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertThat;

public class WirespyRuleTest {
    @Rule
    public WirespyRule wirespyRule = WirespyRule.wirespy(8080)
            .configure(client -> {
                client.from("A", 9091).to("B", 9092).as(HttpProtocol.http());
            })
            .withReport(WirespyReport.wirespyReport(new File("build/wirespy-report")));

    @Test
    public void test() throws Exception {
        Server server = new Server(9092);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write("test".getBytes());
                response.setStatus(200);
                response.getOutputStream().close();
            }
        });
        server.start();

        final HttpResponse<String> response = Unirest.get("http://localhost:9091/test").asString();

        assertThat(response.getBody(), CoreMatchers.is("test"));

        server.stop();
    }

    @Test
    public void test2() throws Exception {
        Server server = new Server(9092);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write("test".getBytes());
                response.setStatus(200);
                response.getOutputStream().close();
            }
        });
        server.start();

        final HttpResponse<String> response = Unirest.get("http://localhost:9091/test").asString();

        assertThat(response.getBody(), CoreMatchers.is("test"));

        server.stop();
    }
}