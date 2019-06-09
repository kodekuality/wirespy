package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.protocol.FrameSequencer;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpResponse;
import rawhttp.core.body.EagerBodyReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class HttpResponseFrameSequencer implements FrameSequencer {
    private final RawHttp rawHttp;

    public HttpResponseFrameSequencer(RawHttp rawHttp) {
        this.rawHttp = rawHttp;
    }

    @Override
    public boolean sequence(InputStream inputStream, Consumer<Frame> receiver) throws IOException {
        RawHttpResponse<Void> response = rawHttp.parseResponse(inputStream).eagerly();
        receiver.accept(new HttpResponseFrame(
                response.getStartLine(),
                response.getHeaders(),
                response.eagerly().getBody().map(EagerBodyReader::asRawBytes)
        ));

        return true;
    }
}
