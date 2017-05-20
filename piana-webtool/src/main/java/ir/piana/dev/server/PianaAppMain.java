package ir.piana.dev.server;

import ir.piana.dev.server.http.BaseHttpServer;

import java.io.InputStream;

/**
 * @author Mohammad Rahmati, 4/23/2017 12:13 PM
 */
public abstract class PianaAppMain {
    private static BaseHttpServer httpServer = null;

    public static void startHttpServer(
            InputStream serverConfigStream,
            InputStream routerConfigStream)
            throws Exception {
        if(httpServer == null) {
            httpServer =
                    BaseHttpServer.createServer(
                            serverConfigStream,
                            routerConfigStream);
        }
        httpServer.start();
    }

    public static void stopHttpServer()
            throws Exception {
        if(httpServer != null) {
            httpServer.stop();
        }
    }
}
