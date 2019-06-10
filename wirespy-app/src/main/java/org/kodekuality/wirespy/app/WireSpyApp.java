package org.kodekuality.wirespy.app;


import org.kodekuality.wirespy.proxy.WireSpyProxy;

public class WireSpyApp {
    public static void main(String[] args) throws Exception {
        WireSpyProxy.from(Integer.parseInt(getString("INPUT_PORT", "9090")))
                .to(getString("TARGET_HOST", "localhost"), Integer.parseInt(getString("TARGET_PORT", "8080")))
                .spyOn(
                        Integer.parseInt(getString("SPY_INBOUND", "9191")),
                        Integer.parseInt(getString("SPY_INBOUND", "9192"))
                )
                .startProxy()
                .awaitTermination();
    }

    private static String getString(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().equals("")) {
            return System.getProperty(name, defaultValue);
        } else {
            return value;
        }
    }
}
