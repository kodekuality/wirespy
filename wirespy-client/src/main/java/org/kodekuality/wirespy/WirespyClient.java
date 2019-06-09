package org.kodekuality.wirespy;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.messages.MessageCollector;
import org.kodekuality.wirespy.protocol.Protocol;
import org.kodekuality.wirespy.service.WirespyCaptureService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class WirespyClient {
    private static final MessageCollector MESSAGE_COLLECTOR = new MessageCollector();

    public static WirespyClient wirespyClient(String host, int port) {
        return wirespyClient(host, port, MESSAGE_COLLECTOR);
    }
    public static WirespyClient wirespyClient(String host, int port, MessageCollector messageCollector) {
        return new WirespyClient(host, port, messageCollector, new WirespyCaptureService(messageCollector));
    }
    public static WirespyClient wirespyClient(int port) {
        return wirespyClient("localhost", port);
    }

    private final String host;
    private final int port;
    private final HttpClient httpClient = HttpClients.createDefault();
    private final MessageCollector messageCollector;
    private final WirespyCaptureService captureService;

    WirespyClient(String host, int port, MessageCollector messageCollector, WirespyCaptureService captureService) {
        this.host = host;
        this.port = port;
        this.messageCollector = messageCollector;
        this.captureService = captureService;
    }

    public void reset () {
        messageCollector.reset();
    }

    public List<Message> allTraffic() {
        return messageCollector.getMessages();
    }

    public FromProxyBuilder from(String name, int proxyPort) {
        return new FromProxyBuilder(name, proxyPort);
    }

    public class FromProxyBuilder {
        private final String sourceName;
        private final int sourcePort;

        public FromProxyBuilder(String name, int sourcePort) {
            this.sourceName = name;
            this.sourcePort = sourcePort;
        }

        public ToProxyBuilder to (String name, String host, int port) {
            return new ToProxyBuilder(sourceName, sourcePort, name, host, port);
        }

        public ToProxyBuilder to (String name, int targetPort) {
            return to(name,"localhost", targetPort);
        }

        public ToProxyBuilder to (String name, String targetHost) {
            return to(name, targetHost, sourcePort);
        }
    }

    public class ToProxyBuilder {
        private final String sourceName;
        private final int sourcePort;
        private final String targetName;
        private final String targetHost;
        private final int targetPort;

        public ToProxyBuilder(String sourceName, int sourcePort, String targetName, String targetHost, int targetPort) {
            this.sourceName = sourceName;
            this.sourcePort = sourcePort;
            this.targetName = targetName;
            this.targetHost = targetHost;
            this.targetPort = targetPort;
        }

        public void as (Protocol protocol) {
            try {
                HttpPost request = new HttpPost(String.format("http://%s:%d/__admin/add", host, port));
                request.setEntity(new StringEntity(
                        new JSONObject()
                                .put("sourceName", sourceName)
                                .put("sourcePort", sourcePort)
                                .put("targetName", targetName)
                                .put("targetHost", targetHost)
                                .put("targetPort", targetPort)
                        .toString()
                ));

                HttpResponse response = httpClient.execute(request);
                JSONObject body = bodyFromResponse(response.getEntity().getContent());

                Socket inStreamSocket = new Socket(host, body.getInt("inStreamPort"));
                Socket outStreamSocket = new Socket(host, body.getInt("outStreamPort"));

                captureService.create(new WirespyCaptureService.CaptureStream(
                        sourceName,
                        inStreamSocket,
                        protocol.getSendSequencer()
                ), new WirespyCaptureService.CaptureStream(
                        targetName,
                        outStreamSocket,
                        protocol.getReceiveSequencer()
                ));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private JSONObject bodyFromResponse(InputStream inputStream) throws IOException {
        BufferedReader bR = new BufferedReader(  new InputStreamReader(inputStream));
        String line;
        StringBuilder responseStrBuilder = new StringBuilder();
        while((line =  bR.readLine()) != null){
            responseStrBuilder.append(line);
        }
        inputStream.close();

        return new JSONObject(responseStrBuilder.toString());
    }


}
