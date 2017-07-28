package ir.piana.dev.webtool2.server;

import ir.piana.dev.webtool2.server.http.BaseHttpServer;
import ir.piana.dev.webtool2.server.annotation.AnnotationController;
import ir.piana.dev.webtool2.server.annotation.PianaServer;
import ir.piana.dev.webtool2.server.annotation.PianaServerCORS;
import ir.piana.dev.webtool2.server.annotation.PianaServerSession;

/**
 * @author Mohammad Rahmati, 4/23/2017 12:13 PM
 */
@PianaServer()
public abstract class PianaAppMain2 {
    private static BaseHttpServer httpServer = null;

    public static void main(String[] args)
            throws Exception {
        Class serverClass = AnnotationController.getServerClass();
        PianaServer  pianaServerAnnotation =
                (PianaServer)serverClass.getAnnotation(
                        PianaServer.class);
        System.out.println(serverClass.toString());
        System.out.println(pianaServerAnnotation.toString());
    }
}