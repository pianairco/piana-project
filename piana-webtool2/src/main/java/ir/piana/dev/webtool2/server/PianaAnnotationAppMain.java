package ir.piana.dev.webtool2.server;

import ir.piana.dev.webtool2.server.annotation.*;
import ir.piana.dev.webtool2.server.http.BaseHttpServer;

/**
 * @author Mohammad Rahmati, 4/23/2017 12:13 PM
 */
@PianaServer(outputClassPath = "d:/classes")
public abstract class PianaAnnotationAppMain {
    private static BaseHttpServer httpServer = null;

    public static void main(String[] args)
            throws Exception {
        Class serverClass = AnnotationController.getServerClass();
        PianaServer  pianaServer =
                (PianaServer)serverClass.getAnnotation(
                        PianaServer.class);
        System.out.println(serverClass.toString());
        System.out.println(pianaServer.toString());
        httpServer = BaseHttpServer.createServer(pianaServer);
        httpServer.start();
    }
}