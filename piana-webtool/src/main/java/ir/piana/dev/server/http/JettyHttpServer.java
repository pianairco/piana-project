package ir.piana.dev.server.http;

import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaServerConfig;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

/**
 * @author Mohammad Rahmati, 7/3/2017 6:32 AM
 */
public class JettyHttpServer
        extends BaseHttpServer {
    private final static Logger logger =
            Logger.getLogger(JettyHttpServer.class);
    private Server server;

    JettyHttpServer(
            PianaServerConfig serverConfig,
            PianaRouterConfig routerConfig) {
        this.serverConfig = serverConfig;
        this.routerConfig = routerConfig;
        this.sessionConfig = serverConfig.getSessionConfig();
    }

    @Override
    protected void startService() {
        logger.info("initializing http server....");
        server = JettyHttpContainerFactory.createServer(
                serverConfig.getBaseUri(), resourceConfig);
    }

    @Override
    protected void stopService() throws Exception {
        server.stop();
    }
}
