package ir.piana.dev.webtool2.server.handler;

import ir.piana.dev.webtool2.server.asset.PianaAsset;
import ir.piana.dev.webtool2.server.response.PianaResponse;
import ir.piana.dev.webtool2.server.annotation.Handler;
import ir.piana.dev.webtool2.server.annotation.HandlerType;
import ir.piana.dev.webtool2.server.annotation.MethodHandler;
import ir.piana.dev.webtool2.server.session.Session;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by SYSTEM on 7/30/2017.
 */
@Handler(baseUrl = "hello-world", handlerType = HandlerType.METHOD_HANDLER)
public class OneMethodHandler {

    @MethodHandler
    @Path("{name}")
//    @Consumes(MediaType.APPLICATION_JSON)
    public static PianaResponse getHello(Session session, @PathParam("name") String name/*, PianaAsset asset*/) {
        return new PianaResponse(Response.Status.OK, 1, name);
    }
}
