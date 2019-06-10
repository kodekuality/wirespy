package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.protocol.FrameSequencer;
import rawhttp.core.EagerHttpRequest;
import rawhttp.core.RawHttp;
import rawhttp.core.body.EagerBodyReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class HttpRequestFrameSequencer implements FrameSequencer {
    private final RawHttp rawHttp;

    public HttpRequestFrameSequencer(RawHttp rawHttp) {
        this.rawHttp = rawHttp;
    }

    @Override
    public void sequence(InputStream inputStream, Consumer<Frame> frameConsumer) throws IOException {
        try {
            while (true) {
                final EagerHttpRequest request = rawHttp.parseRequest(inputStream).eagerly();
                frameConsumer.accept(new HttpRequestFrame(
                        request.getStartLine(),
                        request.getHeaders(),
                        request.getBody().map(EagerBodyReader::asRawBytes)
                ));
            }
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new IOException(e);
        }
    }
}
