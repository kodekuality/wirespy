package org.kodekuality.wirespy.app;

import org.kodekuality.wirespy.WirespyServer;

public class WirespyApp {
    public static void main(String[] args) throws Exception {
        WirespyServer.wirespyServer(8080).start().join();
    }
}
