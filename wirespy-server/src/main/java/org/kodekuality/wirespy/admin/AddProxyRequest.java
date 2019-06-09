package org.kodekuality.wirespy.admin;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddProxyRequest {
    @JsonProperty("sourceName")
    private final String sourceName;
    @JsonProperty("sourcePort")
    private final int sourcePort;
    @JsonProperty("targetName")
    private final String targetName;
    @JsonProperty("targetHost")
    private final String targetHost;
    @JsonProperty("targetPort")
    private final int targetPort;

    public AddProxyRequest(@JsonProperty("sourceName") String sourceName,
                           @JsonProperty("sourcePort") int sourcePort,
                           @JsonProperty("targetName") String targetName,
                           @JsonProperty("targetHost") String targetHost,
                           @JsonProperty("targetPort") int targetPort) {
        this.sourceName = sourceName;
        this.sourcePort = sourcePort;
        this.targetName = targetName;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
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
}
