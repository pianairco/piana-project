package ir.piana.dev.server.filter.response;

import ir.piana.dev.server.config.PianaServerConfig;
import ir.piana.dev.server.config.PianaServerConfig.PianaCORSConfig;

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
    private PianaServerConfig serverConfig = null;
    private PianaCORSConfig corsConfig = null;

    @PostConstruct
    public void init() {
        serverConfig = (PianaServerConfig) config
                .getProperty(PianaServerConfig
                        .PIANA_SERVER_CONFIG);
        corsConfig = serverConfig.getCORSConfig();
    }

    public void filter(ContainerRequestContext request,
                       ContainerResponseContext response)
            throws IOException {
        response.getHeaders().addAll(
                "Access-Control-Allow-Origin",
                corsConfig.getAccessControlAllowOrigin());
        response.getHeaders().addAll(
                "Access-Control-Allow-Headers",
                corsConfig.getAccessControlAllowHeaders());
        response.getHeaders().add(
                "Access-Control-Allow-Credentials",
                corsConfig.getAccessControlAllowCredentials());
        response.getHeaders().addAll(
                "Access-Control-Allow-Methods",
                corsConfig.getAccessControlAllowMethods());
    }
}
