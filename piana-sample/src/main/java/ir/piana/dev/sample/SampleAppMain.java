package ir.piana.dev.sample;

import ir.piana.dev.server.PianaAppMain;

import java.io.InputStream;

/**
 * @author Mohammad Rahmati, 5/22/2017 7:21 AM
 */
public class SampleAppMain {
    public static void startApp()
            throws Exception {
        InputStream serverConfigStream = SampleAppMain.class
                .getResourceAsStream("/piana-server-config.json");
        InputStream routeConfigStream = SampleAppMain.class
                .getResourceAsStream("/piana-route-config.json");
        PianaAppMain.startHttpServer(
                serverConfigStream,
                routeConfigStream);
    }

    public static void stopApp()
            throws Exception {
        PianaAppMain.stopHttpServer();
    }

    public static void main(String[] args)
            throws Exception {
        startApp();
    }
}
