package org.kodekuality.wirespy.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddProxyRequest {
    @JsonProperty("sourceName")
    private final String sourceName;
    @JsonProperty("sourcePort")
    private final int sourcePort;
    @JsonProperty("inStreamPort")
    private final int inStreamPort;
    @JsonProperty("targetName")
    private final String targetName;
    @JsonProperty("targetHost")
    private final String targetHost;
    @JsonProperty("targetPort")
    private final int targetPort;
    @JsonProperty("outStreamPort")
    private final int outStreamPort;

    public AddProxyRequest(@JsonProperty("sourceName") String sourceName,
                           @JsonProperty("sourcePort") int sourcePort,
                           @JsonProperty("inStreamPort") int inStreamPort,
                           @JsonProperty("targetName") String targetName,
                           @JsonProperty("targetHost") String targetHost,
                           @JsonProperty("targetPort") int targetPort,
                           @JsonProperty("outStreamPort") int outStreamPort) {
        this.sourceName = sourceName;
        this.sourcePort = sourcePort;
        this.inStreamPort = inStreamPort;
        this.targetName = targetName;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.outStreamPort = outStreamPort;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public int getInStreamPort() {
        return inStreamPort;
    }

    public int getOutStreamPort() {
        return outStreamPort;
    }
}
