package org.kodekuality.wirespy.watcher;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;
import org.kodekuality.wirespy.protocol.http.HttpProtocol;
import org.kodekuality.wirespy.proxy.WireSpyProxy;
import org.kodekuality.wirespy.proxy.session.WireSpyProxySession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class WireSpyWatcherTest {
    @Test
    public void test() throws Exception {
        Server server = new Server(8080);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().print("Hi!");
                response.setStatus(200);
                response.getOutputStream().close();
            }
        });
        server.start();

        WireSpyProxySession proxySession = WireSpyProxy.from(9090)
                .to("localhost", 8080)
                .spyOn(9191, 9192)
                .startProxy();

        WireSpyWatcher wireSpyWatcher = WireSpyWatcher.newWatcher()
                .watch("localhost")
                .from("UI", 9191)
                .to("APP", 9192)
                .as(HttpProtocol.http())
                .publishTo(System.out::println);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse anotherResponse = httpClient.execute(new HttpGet("http://localhost:9090/"));
        System.out.println(convert(anotherResponse.getEntity().getContent(), Charset.defaultCharset()));
        httpClient.close();
    }


    public String convert(InputStream inputStream, Charset charset) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}