# Wirespy

Wirespy is an inspectable and programmable TCP Proxy.

[![Build Status](https://travis-ci.org/kodekuality/wirespy.svg?branch=master)](https://travis-ci.org/kodekuality/wirespy)
[![codecov](https://codecov.io/gh/kodekuality/wirespy/branch/master/graph/badge.svg)](https://codecov.io/gh/kodekuality/wirespy)
[![GitHub release](https://img.shields.io/github/release/kodekuality/wirespy.svg)](https://GitHub.com/kodekuality/wirespy/releases/)

# Releases

| Module | Version |
|--------|---------|
| Server | [ ![Download](https://api.bintray.com/packages/kodekuality/maven/wirespy-server/images/download.svg) ](https://bintray.com/kodekuality/maven/wirespy-server/_latestVersion) |
| Client | [ ![Download](https://api.bintray.com/packages/kodekuality/maven/wirespy-client/images/download.svg) ](https://bintray.com/kodekuality/maven/wirespy-client/_latestVersion) |
| Report | [ ![Download](https://api.bintray.com/packages/kodekuality/maven/wirespy-report/images/download.svg) ](https://bintray.com/kodekuality/maven/wirespy-report/_latestVersion) |
| Junit4 | [ ![Download](https://api.bintray.com/packages/kodekuality/maven/wirespy-junit4/images/download.svg) ](https://bintray.com/kodekuality/maven/wirespy-junit4/_latestVersion) |

## Why?

Wirespy is a TCP which allows you to inspect all traffic flowing through it. It is a powerful tool to capture data flowing in the network.


## How?

Wirespy is divided into two main components. 
The server and the client. 
The server is at the core of the capture. 
It exposes a JSON Admin interface which allows clients to setup new proxies.
The process can be described as following:

- Clients request for new TCP session (port, targetHost, targetPort)
- Server responds with two new port numbers
  - In Stream Port - which client needs to connect to receive all inbound traffic
  - Out Stream Port - which clients needs to connect to receive all outbound traffic
- Client receives the raw TCP traffic, sequences it into messages (is aware of higher level protocols) and stores in memory

## Quick Start Guide

Include dependencies

    <dependency>
        <groupId>org.kodekuality</groupId>
        <artifactId>wirespy-server</artifactId>
        <version>${wirespy.version}</version>
    </dependency>
    <dependency>
        <groupId>org.kodekuality</groupId>
        <artifactId>wirespy-client</artifactId>
        <version>${wirespy.version}</version>
    </dependency>
    
    Check versions above

Start server and interact with it using the client.

```java
try (WirespyServer wirespyServer = WirespyServer.wirespyServer(9191).start()) {
    WirespyClient wirespyClient = WirespyClient.wirespyClient(9191);
    wirespyClient
            .from("UI", 8081)
            .to("APP", "localhost", 9090)
            .as(HttpProtocol.http());
    wirespyClient
            .from("APP", 13123)
            .to("FX", "localhost", 13124)
            .as(FixProtocol.fix());
    
    // Perform interactions with 8081 (entry point)
    
    List<Message> messages = wirespyClient.allTraffic();
}
```

Full example [here](https://github.com/kodekuality/wirespy/blob/master/wirespy-client/src/test/java/org/kodekuality/wirespy/WirespyClientTest.java#L39)
