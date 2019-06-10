package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Protocol;
import rawhttp.core.RawHttp;

public class HttpProtocol {
    private static final RawHttp rawHttp = new RawHttp();

    public static Protocol http () {
        return new Protocol(
                new HttpRequestFrameSequencer(rawHttp),
                new HttpResponseFrameSequencer(rawHttp)
        );
    }
}
