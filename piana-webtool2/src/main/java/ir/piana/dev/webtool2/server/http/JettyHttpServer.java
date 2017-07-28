package ir.piana.dev.webtool2.server.http;

import ir.piana.dev.webtool2.server.annotation.PianaServer;
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

    JettyHttpServer(PianaServer pianaServer) {
        this.pianaServer = pianaServer;
    }

    @Override
    protected void startService() {
        logger.info("initializing http server....");
        server = JettyHttpContainerFactory.createServer(
                getBaseUri(pianaServer), resourceConfig);
    }

    @Override
    protected void stopService() throws Exception {
        server.stop();
    }
}
