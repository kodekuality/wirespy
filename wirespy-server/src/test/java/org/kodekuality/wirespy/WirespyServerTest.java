package org.kodekuality.wirespy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kodekuality.wirespy.admin.AddProxyRequest;
import org.kodekuality.wirespy.admin.AddProxyResponse;
import org.kodekuality.wirespy.support.EchoServer;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;

public class WirespyServerTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    static {
        Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
            @Override
            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return mapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public String writeValue(Object value) {
                try {
                    return mapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void noConnectionToInSpy() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0).start()) {

            HttpResponse<AddProxyResponse> response = Unirest.post(String.format("http://localhost:%d/__admin/add", wirespyServer.getPort()))
                    .body(new AddProxyRequest(
                            "SystemA", 8081,
                            0, "SystemB", "localhost", 8082,
                            0))
                    .asObject(AddProxyResponse.class);

            expectedException.expect(TimeoutException.class);

            EXECUTOR_SERVICE
                    .submit(() -> new Socket("localhost", 8081).getInputStream().read())
                    .get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void noConnectionToOutSpy() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0).start()) {

            HttpResponse<AddProxyResponse> response = Unirest.post(String.format("http://localhost:%d/__admin/add", wirespyServer.getPort()))
                    .body(new AddProxyRequest(
                            "SystemA", 8081,
                            0, "SystemB", "localhost", 8082,
                            0))
                    .asObject(AddProxyResponse.class);

            Socket inStream = new Socket("localhost", response.getBody().getInStreamPort());

            expectedException.expect(TimeoutException.class);

            EXECUTOR_SERVICE
                    .submit(() -> new Socket("localhost", 8081).getInputStream().read())
                    .get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void noServiceListeningOnOtherSide() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0).start()) {
            HttpResponse<AddProxyResponse> response = Unirest.post(String.format("http://localhost:%d/__admin/add", wirespyServer.getPort()))
                    .body(new AddProxyRequest(
                            "SystemA", 8081,
                            0, "SystemB", "localhost", 8082,
                            0))
                    .asObject(AddProxyResponse.class);

            Socket inStream = new Socket("localhost", response.getBody().getInStreamPort());
            Socket outStream = new Socket("localhost", response.getBody().getOutStreamPort());

            expectedException.expect(TimeoutException.class);

            EXECUTOR_SERVICE
                    .submit(() -> new Socket("localhost", 8081).getInputStream().read())
                    .get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void noDataExchanged() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0).start(); EchoServer echoServer = EchoServer.create(8082)) {
            HttpResponse<AddProxyResponse> response = Unirest.post(String.format("http://localhost:%d/__admin/add", wirespyServer.getPort()))
                    .body(new AddProxyRequest(
                            "SystemA", 8081,
                            0, "SystemB", "localhost", 8082,
                            0))
                    .asObject(AddProxyResponse.class);

            Socket inStream = new Socket("localhost", response.getBody().getInStreamPort());
            Socket outStream = new Socket("localhost", response.getBody().getOutStreamPort());

            expectedException.expect(TimeoutException.class);

            EXECUTOR_SERVICE
                    .submit(() -> new Socket("localhost", 8081).getInputStream().read())
                    .get(1, TimeUnit.SECONDS);
        }
    }

    @Test
    public void dataExchanged() throws Exception {
        Function<Integer, Integer> inputToOutputTransform = x -> x + 1;

        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0).start(); EchoServer echoServer = EchoServer.create(8082, inputToOutputTransform)) {
            echoServer.start();

            HttpResponse<AddProxyResponse> response = Unirest.post(String.format("http://localhost:%d/__admin/add", wirespyServer.getPort()))
                    .body(new AddProxyRequest(
                            "SystemA", 8081,
                            0, "SystemB", "localhost", 8082,
                            0))
                    .asObject(AddProxyResponse.class);

            Socket inStream = new Socket("localhost", response.getBody().getInStreamPort());
            Socket outStream = new Socket("localhost", response.getBody().getOutStreamPort());

            Socket socket = new Socket("localhost", 8081);

            socket.getOutputStream().write(3);
            socket.getOutputStream().flush();
            assertThat(inStream.getInputStream().read(), Matchers.is(3));

            assertThat(outStream.getInputStream().read(), Matchers.is(4));
            assertThat(socket.getInputStream().read(), Matchers.is(4));
        }
    }

    @Test(expected = TimeoutException.class)
    public void join() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(0)) {
            EXECUTOR_SERVICE.submit(() -> {
                try {
                    wirespyServer.start().join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).get(3, TimeUnit.SECONDS);
        }
    }
}