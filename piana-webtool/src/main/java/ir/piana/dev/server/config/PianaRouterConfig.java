package ir.piana.dev.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Mohammad Rahmati, 4/23/2017 11:41 AM
 */
public class PianaRouterConfig
        extends PianaConfig {
    public static final String PIANA_ROUTER_CONFIG =
            "piana-router-config";
    public static final String METHOD_CONFIG
            = "method";

    private static final Logger logger =
            Logger.getLogger(
                    PianaRouterConfig.class);

    public PianaRouterConfig(
            PianaConfig pianaConfig) {
        reconfigure(pianaConfig);
    }

    public Set<String> getUrlPatterns() {
        return configMap.keySet();
    }

    public Set<String> getHttpMethodPatterns(
            String urlPattern) {
        if (urlPattern == null || urlPattern.isEmpty())
            urlPattern = new String("/");
        if (getPianaConfig(urlPattern) == null)
            return null;
        Map<String, Object> configMap = getMap(urlPattern);
        if (configMap != null)
            return configMap.keySet();
        return null;
    }

    public JsonNode getJsonNode() {
        return this.jsonNode;
    }

    public PianaRouteConfig getRouteConfig(
            String urlPattern, String methodPattern) {
        if (urlPattern == null || urlPattern.isEmpty())
            urlPattern = new String("/");
        if (getPianaConfig(urlPattern) == null)
            return null;
        Map<String, Object> configMap = getMap(urlPattern);
        if (configMap.containsKey(methodPattern)) {
            PianaRouteConfig routeConfig =
                    new PianaRouteConfig();
            routeConfig.reconfigure((Map<String, Object>)
                    configMap.get(methodPattern));
            return routeConfig;
        }
        return null;
    }

    public static class PianaRouteConfig
            extends PianaConfig {
        public static final String HANDLER_CONFIG
                = "handler";
        public static final String PATH_PARAMS_CONFIG
                = "path-params";
        public static final String QUERY_PARAMS_CONFIG
                = "query-params";
        public static final String ASYNC_CONFIG
                = "async";
        public static final String BODY_JSON_OBJECT_CONFIG
                = "body-json-object";
        public static final String ROLE_CONFIG
                = "role";
        public static final String URL_INJECTED_CONFIG
                = "url-injected";
        public static final String ASSET_PATH_CONFIG
                = "asset-path";
        public static final String DOC_NAME_CONFIG
                = "doc-name";

        public String getHandler() {
            return getString(HANDLER_CONFIG);
        }

        public List<String> getPathParams() {
            return getList(PATH_PARAMS_CONFIG);
        }

        public List<String> getQueryParams() {
            return getList(QUERY_PARAMS_CONFIG);
        }

        public String getBodyJsonObject() {
            return getString(BODY_JSON_OBJECT_CONFIG);
        }

        public String getRole() {
            return getString(ROLE_CONFIG);
        }

        public boolean isUrlInjected() {
            return Boolean.valueOf(
                    getString(URL_INJECTED_CONFIG));
        }

        public boolean isAsync() {
            return Boolean.valueOf(
                    getString(ASYNC_CONFIG));
        }

        public String getAssetPath() {
            return getString(ASSET_PATH_CONFIG);
        }

        public boolean isAsset() {
            if (getString(ASSET_PATH_CONFIG) != null) {
                File file = new File(
                        getString(ASSET_PATH_CONFIG));
                if (!file.exists()){
                    logger.error("asset path not is correct path.");
                } else if(!file.isDirectory()) {
                    logger.error("asset path not is a directory.");
                } else
                    return true;
            }
            return false;
        }

        public String getDocName() {
            if(getString(DOC_NAME_CONFIG) == null)
                return "";
            return getString(DOC_NAME_CONFIG);
        }

        public boolean hasDocName() {
            if (getString(DOC_NAME_CONFIG) != null) {
                return true;
            }
            return false;
        }
    }
}
