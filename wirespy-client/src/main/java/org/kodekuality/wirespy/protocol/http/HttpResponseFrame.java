package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import rawhttp.core.RawHttpHeaders;
import rawhttp.core.StatusLine;

import java.util.Optional;

public class HttpResponseFrame implements Frame {
    private final StatusLine statusLine;
    private final RawHttpHeaders headers;
    private final Optional<byte[]> content;

    public HttpResponseFrame(StatusLine startLine, RawHttpHeaders headers, Optional<byte[]> content) {
        this.statusLine = startLine;
        this.headers = headers;
        this.content = content;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public RawHttpHeaders getHeaders() {
        return headers;
    }

    public Optional<byte[]> getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "HttpResponseFrame{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", content=" + content +
                '}';
    }
}
