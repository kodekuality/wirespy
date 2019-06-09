package org.kodekuality.wirespy;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.text.MatchesPattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.protocol.fix.FixProtocol;
import org.kodekuality.wirespy.protocol.http.HttpProtocol;
import org.kodekuality.wirespy.support.FixEchoServer;
import org.kodekuality.wirespy.support.FixServer;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class WirespyClientTest {
    private FixEchoServer fixEchoServer;
    private FixServer fixServer;

    @Before
    public void setUp() throws Exception {
        fixServer = new FixServer(13124);
        fixServer.start();

        fixEchoServer = new FixEchoServer(9090, 13123);
        fixEchoServer.start();
    }

    @After
    public void tearDown() throws Exception {
        fixServer.stop();
        fixEchoServer.stop();
    }

    @Test
    public void conversationAsExpected() throws Exception {
        try (WirespyServer wirespyServer = WirespyServer.wirespyServer(9191).start()) {
            WirespyClient wirespyClient = WirespyClient.wirespyClient(9191);
            wirespyClient
                    .from("UI", 8081)
                    .to("APP", "localhost", 9090)
                    .as(HttpProtocol.http());
            wirespyClient
                    .from("APP", 13123)
                    .to("FX", "localhost", 13124)
                    .as(FixProtocol.fix());

            HttpResponse<String> response = Unirest.post("http://localhost:8081")
                    .body(
                            "8=FIX.4.4\u00019=290\u000135=D\u000134=14\u000149=TestClient2\u000152=20080228-14:52:00.062\u000156=FXCM\u000157=MINIDEMO\u00011=00286255\u000111=TestClient2FXCM-1204210320062-12\u000138=100000\u000140=3\u000144=1.51524\u000154=1\u000155=EUR/USD\u000159=1\u000160=20080228-14:52:00\u0001117=100004024407\u0001526=\u0001386=1\u0001336=FXCM\u0001625=MINIDEMO\u0001453=1\u0001448=FXCM ID\u0001447=D\u0001452=3\u0001802=1\u0001523=286255\u0001803=10\u000110=151\u0001"
                    ).asString();

            List<Message> messages = wirespyClient.allTraffic();

            assertThat(response.getBody(), MatchesPattern.matchesPattern("^8=.*"));
            assertThat(messages.size(), CoreMatchers.is(Matchers.greaterThan(6)));
        }
    }
}