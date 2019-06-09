package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.FrameSequencer;
import org.kodekuality.wirespy.protocol.Protocol;
import rawhttp.core.RawHttp;

public class HttpProtocol implements Protocol {
    public static HttpProtocol http() {
        return new HttpProtocol();
    }

    private final RawHttp rawHttp = new RawHttp();

    @Override
    public FrameSequencer getSendSequencer() {
        return new HttpRequestFrameSequencer(rawHttp);
    }

    @Override
    public FrameSequencer getReceiveSequencer() {
        return new HttpResponseFrameSequencer(rawHttp);
    }
}
