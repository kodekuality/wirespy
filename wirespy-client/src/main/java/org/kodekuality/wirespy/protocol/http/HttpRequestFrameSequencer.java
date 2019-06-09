package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import org.kodekuality.wirespy.protocol.FrameSequencer;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class HttpRequestFrameSequencer implements FrameSequencer {
    private final RawHttp rawHttp;

    public HttpRequestFrameSequencer(RawHttp rawHttp) {
        this.rawHttp = rawHttp;
    }

    @Override
    public boolean sequence(InputStream inputStream, Consumer<Frame> receiver) throws IOException {
        RawHttpRequest rawHttpRequest = rawHttp.parseRequest(inputStream);
        receiver.accept(new HttpRequestFrame(
                rawHttpRequest.getStartLine(),
                rawHttpRequest.getHeaders(),
                rawHttpRequest.getBody()
                        .map(x -> {
                            try {
                                return x.asRawBytes();
                            } catch (IOException e) {
                                e.printStackTrace();
                                return new byte[]{};
                            }
                        })

        ));

        return true;
    }
}
