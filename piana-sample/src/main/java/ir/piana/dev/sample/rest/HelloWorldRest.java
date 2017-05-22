package ir.piana.dev.sample.rest;

import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Mohammad Rahmati, 5/22/2017 7:25 AM
 */
public class HelloWorldRest {
    public static PianaResponse getHelloWorld(Session session) {
        return new PianaResponse(Response.Status.OK,
                "Hello World!",
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getHelloToName(
            Session session,
            String name) {
        return new PianaResponse(Response.Status.OK,
                "Hello ".concat(name),
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getMessageToNameFamily(
            Session session,
            String name, String family,
            String message) {
        return new PianaResponse(Response.Status.OK,
                message.concat(" ")
                        .concat(name)
                        .concat(" ")
                        .concat(family),
                MediaType.TEXT_PLAIN);
    }
}
