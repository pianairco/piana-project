package ir.piana.dev.webtool2.server.http;

import ir.piana.dev.webtool2.server.annotation.PianaServer;
import ir.piana.dev.webtool2.server.annotation.PianaServerCORS;
import ir.piana.dev.webtool2.server.annotation.PianaServerSession;
import ir.piana.dev.webtool2.server.filter.response.CORSFilter;
import ir.piana.dev.webtool2.server.route.RouteClassGenerator;
import ir.piana.dev.webtool2.server.session.SessionManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Mohammad Rahmati, 4/23/2017 11:36 AM
 */
public abstract class BaseHttpServer {
    protected Logger logger = Logger
            .getLogger(BaseHttpServer.class);
    protected Map<String, Object> httpProperties
            = new LinkedHashMap<>();
    protected ResourceConfig resourceConfig
            = new ResourceConfig();
    protected SessionManager sessionManager = null;
    private boolean isStart = false;
    protected PianaServer pianaServer;

    public static BaseHttpServer createServer(
            PianaServer pianaServer)
            throws Exception {
        HttpServerType serverType = pianaServer.serverType();

        if(HttpServerType.NETTY == serverType)
            return new NettyHttpServer(
                    pianaServer);
        else if(HttpServerType.JETTY == serverType)
            return new JettyHttpServer(
                    pianaServer);
        else
            throw new Exception("type of http server not founded.");
    }

    public void addProperties(
            String key, Object property) {
        httpProperties.put(key, property);
    }

    public void start()
            throws Exception {
        if(isStart) {
            logger.error("server is started already.");
            return;
        }

        Set<Class<?>> handlerClasses = RouteClassGenerator
                .generateHandlerClasses(pianaServer);
        resourceConfig.registerClasses(handlerClasses);

//        Set<Class<?>> routeClasses = RouteClassGenerator
//                    .generateRouteClasses(pianaServer);
//
//        Set<Class<?>> documentClasses = RouteClassGenerator
//                .generateDocumentClasses(pianaServer);

//        if(serverConfig.hasDocPath()) {
//            DocumentResolver.initialize(
//                    serverConfig.getDocPath());
//        }

//        resourceConfig.registerClasses(routeClasses);
//        resourceConfig.registerClasses(documentClasses);
        resourceConfig.register(CORSFilter.class);

        sessionManager = SessionManager
                .createSessionManager(pianaServer.serverSession());

        httpProperties.put(
                "PIANA_SERVER_CONFIG",
                pianaServer);
//        httpProperties.put(
//                "PIANA_ROUTER_CONFIG",
//                routerConfig);
        httpProperties.put(
                SessionManager.PIANA_SESSION_MANAGER,
                sessionManager);
        resourceConfig.addProperties(httpProperties);
        startService();
        isStart = true;
    }

    protected abstract void startService();

    public void stop()
            throws Exception {
        if(isStart) {
            stopService();
            isStart = false;
        } else {
            logger.error("server is not started already.");
        }
    }

    protected abstract void stopService()
            throws Exception;

    public URI getBaseUri(PianaServer pianaServer) {
        return UriBuilder.fromUri("http://"
                .concat(pianaServer.httpIp())
                .concat(":")
                .concat(String.valueOf(pianaServer.httpPort()))
                .concat("/")
                .concat(pianaServer.httpBaseRoute()))
                .build();
    }
}
