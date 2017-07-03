package ir.piana.dev.server.http;

import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaServerConfig;
import ir.piana.dev.server.config.PianaServerConfig.PianaSessionConfig;
import io.netty.channel.Channel;
import org.apache.log4j.Logger;
import org.glassfish.jersey.netty.httpserver.NettyHttpContainerProvider;

/**
 * @author Mohammad Rahmati, 4/12/2017 2:21 PM
 */
class NettyHttpServer
        extends BaseHttpServer {
    private final static Logger logger =
            Logger.getLogger(NettyHttpServer.class);
    private Channel channel = null;

    NettyHttpServer(
            PianaServerConfig serverConfig,
            PianaRouterConfig routerConfig) {
        this.serverConfig = serverConfig;
        this.routerConfig = routerConfig;
        this.sessionConfig = serverConfig.getSessionConfig();
    }

    @Override
    protected void startService() {
        logger.info("initializing http server....");
        channel = NettyHttpContainerProvider
                .createServer(serverConfig.getBaseUri(),
                        resourceConfig, false);
        logger.info("http server started....");
    }

    @Override
    protected void stopService()
            throws InterruptedException {
        channel.closeFuture();
        logger.info("http server stopped....");
    }
}
