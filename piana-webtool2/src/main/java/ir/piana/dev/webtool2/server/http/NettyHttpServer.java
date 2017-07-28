package ir.piana.dev.webtool2.server.http;

import io.netty.channel.Channel;
import ir.piana.dev.webtool2.server.annotation.PianaServer;
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

    NettyHttpServer(PianaServer pianaServer) {
        this.pianaServer = pianaServer;
    }

    @Override
    protected void startService() {
        logger.info("initializing http server....");
        channel = NettyHttpContainerProvider
                .createServer(getBaseUri(pianaServer),
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
