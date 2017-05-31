package ir.piana.dev.server.config;

import ir.piana.dev.server.http.HttpServerType;
import org.apache.log4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.List;

/**
 * @author Mohammad Rahmati, 4/23/2017 11:41 AM
 */
public class PianaServerConfig
        extends PianaConfig {
    public static final String PIANA_SERVER_CONFIG =
            "piana-server-config";
    private static final Logger logger =
            Logger.getLogger(
                    PianaServerConfig.class);
    public static final String SERVER_TYPE
            = "server-type";
    public static final String HTTP_IP
            = "http-ip";
    public static final String HTTP_BASE_ROUTE
            = "http-base-route";
    public static final String HTTP_PORT
            = "http-port";
    public static final String SESSION_CONFIG
            = "session";
    public static final String CORS_CONFIG
            = "cors";
    public static final String REST_FILTERS
            = "rest-filters";
    public static final String OUTPUT_CLASS_PATH
            = "output-class-path";
    public static final String REMOVE_OTHER_COOKIES
            = "remove-other-cookies";

    public HttpServerType getServerType() {
        if(configMap == null)
            return null;
        return HttpServerType.fromName(
                (String) configMap.get(SERVER_TYPE));
    }

    public String getIP() {
        return getString(HTTP_IP);
    }

    public String getPort() {
        return getString(HTTP_PORT);
    }

    public String getBaseRoute() {
        return getString(HTTP_BASE_ROUTE);
    }

    public PianaSessionConfig getSessionConfig() {
        PianaSessionConfig sessionConfig =
                new PianaSessionConfig();
        sessionConfig.reconfigure(getPianaConfig(SESSION_CONFIG));
        return sessionConfig;
    }

    public PianaCORSConfig getCORSConfig() {
        PianaCORSConfig corsConfig =
                new PianaCORSConfig();
        corsConfig.reconfigure(getPianaConfig(CORS_CONFIG));
        return corsConfig;
    }

    public String getOutputClassPath() {
        return getString(OUTPUT_CLASS_PATH);
    }

    public boolean isRemoveOtherCookies() {
        if(getString(REMOVE_OTHER_COOKIES) == null
                || getString(REMOVE_OTHER_COOKIES).isEmpty())
            return false;
        return Boolean.getBoolean(getString(REMOVE_OTHER_COOKIES));
    }

    public URI getBaseUri() {
        return UriBuilder.fromUri("http://"
                .concat(getString(HTTP_IP))
                .concat(":")
                .concat(getString(HTTP_PORT))
                .concat("/")
                .concat(getString(
                        HTTP_BASE_ROUTE)))
                .build();
    }

    public static class PianaSessionConfig
            extends PianaConfig {
        public static final String SESSION_NAME
                = "session-name";
        public static final String SESSION_CACHE_SIZE
                = "session-cache-size";
        public static final String SESSION_EXPIRE_SECOND
                = "session-expire-second";

        public String getSessionName() {
            return getString(SESSION_NAME);
        }

        public int getSessionCacheSize() {
            return Integer.parseInt(
                    getString(SESSION_CACHE_SIZE));
        }

        public int getSessionExpireSecond() {
            return Integer.parseInt(getString(
                    SESSION_EXPIRE_SECOND
            ));
        }
    }

    public static class PianaCORSConfig
            extends PianaConfig {
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN
                = "access-control-allow-origin";
        public static final String ACCESS_CONTROL_ALLOW_HEADERS
                = "access-control-allow-headers";
        public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS
                = "access-control-allow-credentials";
        public static final String ACCESS_CONTROL_ALLOW_METHODS
                = "access-control-allow-methods";

        public List<String> getAccessControlAllowOrigin() {
            return getList(ACCESS_CONTROL_ALLOW_ORIGIN);
        }

        public List<String> getAccessControlAllowHeaders() {
            return getList(ACCESS_CONTROL_ALLOW_HEADERS);
        }

        public boolean getAccessControlAllowCredentials() {
            return Boolean.getBoolean(getString(
                    ACCESS_CONTROL_ALLOW_CREDENTIALS
            ));
        }

        public List<String> getAccessControlAllowMethods() {
            return getList(ACCESS_CONTROL_ALLOW_METHODS);
        }
    }
}
