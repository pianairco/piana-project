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
        PianaServerConfig pianaServerConfig =
                new PianaServerConfig();
        pianaServerConfig.reconfigure(PianaConfigReader
                .createFromJson(serverConfigStream));
        PianaRouterConfig pianaRouterConfig =
                new PianaRouterConfig();
        pianaRouterConfig.reconfigure(PianaConfigReader
                .createFromJson(routerConfigStream));
        HttpServerType serverType =
                pianaServerConfig.getServerType();

        if(HttpServerType.NETTY == serverType)
            return new NettyHttpServer(
                    pianaServerConfig, pianaRouterConfig);
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
        Set<Class<?>> classes = RouteClassGenerator
                    .generateRouteClasses(routerConfig,
                            serverConfig.getOutputClassPath());
        resourceConfig.registerClasses(classes);
        resourceConfig.register(CORSFilter.class);

        sessionManager = SessionManager
                .createSessionManager(sessionConfig);

        httpProperties.put(
                PianaServerConfig.PIANA_SERVER_CONFIG,
                serverConfig);
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