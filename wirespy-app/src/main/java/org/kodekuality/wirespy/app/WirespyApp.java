package org.kodekuality.wirespy.app;

import org.kodekuality.wirespy.WirespyServer;

public class WirespyApp {
    public static void main(String[] args) throws Exception {
        WirespyServer.wirespyServer(getPort()).start().join();
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null || port.matches("[0-9]{4,5}")) {
            return 9191;
        } else {
            return Integer.parseInt(port);
        }
    }
}
