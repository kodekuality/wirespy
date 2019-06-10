package org.kodekuality.wirespy.service.tcp;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Test;
import org.kodekuality.wirespy.service.stream.StreamCopier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SpyTcpProxyServiceTest {
    @Test
    public void test() throws Exception {
        final SpyTcpProxyService spyTcpProxyService = new SpyTcpProxyService();
        final Server server = new Server(12312);
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.getOutputStream().write("hi".getBytes());
                response.setStatus(200);
                response.getOutputStream().close();
            }
        });
        server.start();

        SpyTcpSession spyTcpSession = spyTcpProxyService.startSpySession(new SpySource(
                "A",
                12311
        ), new SpyTarget("B", "localhost", 12312), 0, 0);

        ByteArrayOutputStream inStreamMemory = new ByteArrayOutputStream();
        ByteArrayOutputStream outStreamMemory = new ByteArrayOutputStream();
        Socket inStream = new Socket("localhost", spyTcpSession.getInStreamPort());
        Socket outStream = new Socket("localhost", spyTcpSession.getOutStreamPort());

        process(inStreamMemory, inStream, "A", "Finished in");
        process(outStreamMemory, outStream, "B", "Finished out");

        HttpClient client1 = HttpClients.createDefault();
        HttpClient client2 = HttpClients.createDefault();

        HttpResponse response1 = client1.execute(new HttpGet("http://localhost:12311/test"));
        System.out.println(IOUtils.toString(response1.getEntity().getContent()));
        client1.getConnectionManager().closeIdleConnections(0, TimeUnit.MICROSECONDS);


        ByteArrayOutputStream inStreamMemory2 = new ByteArrayOutputStream();
        ByteArrayOutputStream outStreamMemory2 = new ByteArrayOutputStream();
        Socket inStream2 = new Socket("localhost", spyTcpSession.getInStreamPort());
        Socket outStream2 = new Socket("localhost", spyTcpSession.getOutStreamPort());

        process(inStreamMemory2, inStream2, "A2", "Finished in");
        process(outStreamMemory2, outStream2, "B2", "Finished out");

        HttpResponse response2 = client2.execute(new HttpGet("http://localhost:12311/test2"));
        System.out.println(IOUtils.toString(response2.getEntity().getContent()));
        client1.getConnectionManager().closeIdleConnections(0, TimeUnit.MICROSECONDS);

        System.out.println("IN: "+inStreamMemory.toString());
        System.out.println("OUT: "+outStreamMemory.toString());

        System.out.println("IN2: "+inStreamMemory2.toString());
        System.out.println("OUT2: "+outStreamMemory2.toString());
    }

    private void process(ByteArrayOutputStream inStreamMemory, Socket inStream, String a, String s) {
        new Thread(() -> {
            try {
                new StreamCopier(a, inStream.getInputStream(), inStreamMemory).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(s);
        }).start();
    }
}