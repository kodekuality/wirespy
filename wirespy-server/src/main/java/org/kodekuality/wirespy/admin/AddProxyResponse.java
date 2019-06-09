package org.kodekuality.wirespy.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddProxyResponse {
    @JsonProperty("inStreamPort")
    private final int inStreamPort;
    @JsonProperty("outStreamPort")
    private final int outStreamPort;

    public AddProxyResponse(@JsonProperty("inStreamPort") int inStreamPort,
                            @JsonProperty("outStreamPort") int outStreamPort) {
        this.inStreamPort = inStreamPort;
        this.outStreamPort = outStreamPort;
    }

    public int getInStreamPort() {
        return inStreamPort;
    }

    public int getOutStreamPort() {
        return outStreamPort;
    }
}
