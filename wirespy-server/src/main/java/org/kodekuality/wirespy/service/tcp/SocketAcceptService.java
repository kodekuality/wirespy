package org.kodekuality.wirespy.service.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAcceptService {
    private final Logger logger = LoggerFactory.getLogger(SocketAcceptService.class);

    public Socket accept (String from, String to, ServerSocket serverSocket) throws IOException {
        logger.info("Waiting for spy (From {} to {}) to connect to port {}",
                from,
                to,
                serverSocket.getLocalPort()
        );
        return serverSocket.accept();
    }
}
