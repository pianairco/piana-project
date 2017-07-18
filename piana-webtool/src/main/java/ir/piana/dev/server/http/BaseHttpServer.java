package ir.piana.dev.server.http;

import ir.piana.dev.server.config.PianaConfigReader;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaServerConfig;
import ir.piana.dev.server.config.PianaServerConfig.PianaSessionConfig;
import ir.piana.dev.server.filter.response.CORSFilter;
import ir.piana.dev.server.route.RouteClassGenerator;
import ir.piana.dev.server.session.SessionManager;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.InputStream;
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
    protected PianaServerConfig serverConfig = null;
    protected PianaSessionConfig sessionConfig = null;
    protected PianaRouterConfig routerConfig = null;
    protected SessionManager sessionManager = null;
    private boolean isStart = false;

    public static BaseHttpServer createServer(
            InputStream serverConfigStream,
            InputStream routerConfigStream)
            throws Exception {
        PianaServerConfig psConfig = new PianaServerConfig(
                PianaConfigReader.createFromJson(
                        serverConfigStream));
        PianaRouterConfig prConfig = new PianaRouterConfig(
                PianaConfigReader.createFromJson(
                        routerConfigStream));
        HttpServerType serverType =
                psConfig.getServerType();

        if(HttpServerType.NETTY == serverType)
            return new NettyHttpServer(
                    psConfig, prConfig);
        else if(HttpServerType.JETTY == serverType)
            return new JettyHttpServer(
                    psConfig, prConfig);
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
        Set<Class<?>> routeClasses = RouteClassGenerator
                    .generateRouteClasses(routerConfig, serverConfig);

        Class documentClass = RouteClassGenerator
                .generateDocumentClass(routerConfig, serverConfig);

//        if(serverConfig.hasDocPath()) {
//            DocumentResolver.initialize(
//                    serverConfig.getDocPath());
//        }

        resourceConfig.registerClasses(routeClasses);
        resourceConfig.registerClasses(documentClass);
        resourceConfig.register(CORSFilter.class);

        sessionManager = SessionManager
                .createSessionManager(sessionConfig);

        httpProperties.put(
                PianaServerConfig.PIANA_SERVER_CONFIG,
                serverConfig);
        httpProperties.put(
                PianaRouterConfig.PIANA_ROUTER_CONFIG,
                routerConfig);
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
}
