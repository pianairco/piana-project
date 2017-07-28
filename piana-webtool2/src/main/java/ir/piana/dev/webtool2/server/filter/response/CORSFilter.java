package ir.piana.dev.webtool2.server.filter.response;

import ir.piana.dev.webtool2.server.annotation.PianaServer;
import ir.piana.dev.webtool2.server.annotation.PianaServerCORS;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * @author Mohammad Rahmati, 4/12/2017 2:27 PM
 */
@Singleton
public class CORSFilter
        implements ContainerResponseFilter {
    @Context
    private Configuration config;
    private PianaServer pianaServer = null;
    private PianaServerCORS serverCORS = null;

    @PostConstruct
    public void init() {
        pianaServer = (PianaServer) config
                .getProperty("PIANA_SERVER_CONFIG");
        serverCORS = pianaServer.serverCORS();
    }

    public void filter(ContainerRequestContext request,
                       ContainerResponseContext response)
            throws IOException {
        response.getHeaders().addAll(
                "Access-Control-Allow-Origin",
                serverCORS.allowOrigin());
        response.getHeaders().addAll(
                "Access-Control-Allow-Headers",
                serverCORS.allowHeaders());
        response.getHeaders().add(
                "Access-Control-Allow-Credentials",
                serverCORS.allowCredentials());
        response.getHeaders().addAll(
                "Access-Control-Allow-Methods",
                serverCORS.allowMethods());
    }
}
