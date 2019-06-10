package org.kodekuality.wirespy;

import org.kodekuality.wirespy.server.TinyServer;
import org.kodekuality.wirespy.server.WirespyConfiguration;
import org.kodekuality.wirespy.server.WirespyServerFactory;
import org.kodekuality.wirespy.service.concurrency.ConcurrencyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

public class WirespyServer implements Closeable {
    private static final Logger logger = LoggerFactory.getLogger(WirespyServer.class);
    private static final WirespyServerFactory FACTORY = new WirespyServerFactory();

    public static WirespyServer wirespyServer(int port) {
        return new WirespyServer(FACTORY.create(new WirespyConfiguration(port)));
    }

    private TinyServer httpServer;

    public WirespyServer(TinyServer httpServer) {
        this.httpServer = httpServer;
    }

    public WirespyServer start () throws Exception {
        httpServer.start();
        logger.info("Wirespy admin API started on {}", getPort());
        return this;
    }

    public WirespyServer join () throws Exception {
        httpServer.join();
        return this;
    }

    public WirespyServer stop () throws Exception {
        httpServer.stop();
        return this;
    }

    public int getPort () {
        return httpServer.getPort();
    }

    @Override
    public void close() {
        ConcurrencyUtils.runSilently(() -> { this.stop(); return null; } );
    }
}
