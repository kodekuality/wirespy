package org.kodekuality.wirespy.proxy;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;
import org.kodekuality.wirespy.proxy.session.WireSpyProxySession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.stream.Collectors;

public class WireSpyProxyTest {
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

        WireSpyProxySession session1 = WireSpyProxy.from(9090)
                .to("localhost", 9091)
                .spyOn(9191, 9192)
                .startProxy();

        WireSpyProxySession session2 = WireSpyProxy.from(9091)
                .to("localhost", 8080)
                .spyOn(9193, 9194)
                .startProxy();

        Socket inSpy = new Socket("localhost", 9191);
        Socket outSpy = new Socket("localhost", 9192);
        Socket inSpy2 = new Socket("localhost", 9193);
        Socket outSpy2 = new Socket("localhost", 9194);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse anotherResponse = httpClient.execute(new HttpGet("http://localhost:9090/"));
        System.out.println(convert(anotherResponse.getEntity().getContent(), Charset.defaultCharset()));
        httpClient.close();
        System.out.println("Yo");

        Socket in2Spy = new Socket("localhost", 9191);
        Socket out2Spy = new Socket("localhost", 9192);
        Socket in2Spy2 = new Socket("localhost", 9193);
        Socket out2Spy2 = new Socket("localhost", 9194);

        CloseableHttpClient httpClient2 = HttpClients.createDefault();
        CloseableHttpResponse anotherResponse2 = httpClient2.execute(new HttpGet("http://localhost:9090/"));
        System.out.println(convert(anotherResponse2.getEntity().getContent(), Charset.defaultCharset()));
        httpClient2.close();
        System.out.println("Yo2");


        session1.stopProxy();
        session2.stopProxy();
    }

    public String convert(InputStream inputStream, Charset charset) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }
}