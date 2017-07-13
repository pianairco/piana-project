package ir.piana.dev.sample.rest;

import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/22/2017 7:25 AM
 */
public class HelloWorldRest {
    public static PianaResponse getHelloWorld(
            Session session) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PianaResponse(Response.Status.OK,
                "Hello World!",
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getHelloWorld(
            Session session,
            Map<String, List<String>> mapParams) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    public static PianaResponse getHelloToName(
            Session session,
            Map<String, List<String>> mapParams) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PianaResponse(Response.Status.OK,
                "Hello ".concat(mapParams.get("name").get(0)),
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

    public static PianaResponse getMessageToNameFamily(
            Session session,
            Map<String, List<String>> mapParams) {
        MultivaluedMap d = new MultivaluedHashMap(mapParams);
        return new PianaResponse(Response.Status.OK,
                mapParams.get("message").get(0)
                        .concat(" ")
                        .concat(mapParams.get("name").get(0))
                        .concat(" ")
                        .concat(mapParams.get("family").get(0)),
                MediaType.TEXT_PLAIN);
    }
}
