package org.kodekuality.wirespy.report;

import org.junit.Test;
import org.kodekuality.wirespy.messages.Message;
import org.kodekuality.wirespy.protocol.fix.FixFrame;
import org.kodekuality.wirespy.protocol.http.HttpRequestFrame;
import org.kodekuality.wirespy.protocol.http.HttpResponseFrame;
import rawhttp.core.HttpVersion;
import rawhttp.core.RawHttpHeaders;
import rawhttp.core.RequestLine;
import rawhttp.core.StatusLine;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class WirespyReportTest {
    @Test
    public void test() throws IOException, URISyntaxException {
        WirespyReport.wirespyReport(new File("build/complete-test"))
                .generate("index.html", new Request(
                        "Example Report",
                        Arrays.asList(new Message(
                                System.nanoTime(),
                                "One", "Two",
                                new FixFrame(Collections.singletonList(new FixFrame.Field(8, "FIX.4.4")))
                        ), new Message(
                                System.nanoTime(),
                                "Two", "Three",
                                new HttpRequestFrame(
                                        new RequestLine("GET", new URI("http://localhost:8080/example"), HttpVersion.HTTP_1_1),
                                        RawHttpHeaders.empty(),
                                        Optional.empty()
                                )
                        ), new Message(
                                System.nanoTime(),
                                "Three", "Two",
                                new HttpResponseFrame(
                                        new StatusLine(HttpVersion.HTTP_1_1, 200, "OK"),
                                        RawHttpHeaders.empty(),
                                        Optional.of("example".getBytes())
                                )
                        ))
                ))
                ;
    }
}