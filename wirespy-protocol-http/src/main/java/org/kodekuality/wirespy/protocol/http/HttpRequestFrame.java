package org.kodekuality.wirespy.protocol.http;

import org.kodekuality.wirespy.protocol.Frame;
import rawhttp.core.RawHttpHeaders;
import rawhttp.core.RequestLine;

import java.util.Optional;

public class HttpRequestFrame implements Frame {
    private final RequestLine requestLine;
    private final RawHttpHeaders headers;
    private final Optional<byte[]> content;

    public HttpRequestFrame(RequestLine requestLine, RawHttpHeaders headers, Optional<byte[]> content) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.content = content;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RawHttpHeaders getHeaders() {
        return headers;
    }

    public Optional<byte[]> getContent() {
        return content;
    }
}
