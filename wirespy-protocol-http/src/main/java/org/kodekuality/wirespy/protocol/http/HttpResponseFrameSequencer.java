package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.protocol.FrameSequencer;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.EagerHttpResponse;
import rawhttp.core.RawHttp;
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
    public void sequence(InputStream inputStream, Consumer<Frame> frameConsumer) throws IOException {
        try {
            while (true) {
                final EagerHttpResponse<Void> response = rawHttp.parseResponse(inputStream).eagerly();
                frameConsumer.accept(new HttpResponseFrame(
                        response.getStartLine(),
                        response.getHeaders(),
                        response.getBody().map(EagerBodyReader::asRawBytes)
                ));
            }
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }
}
