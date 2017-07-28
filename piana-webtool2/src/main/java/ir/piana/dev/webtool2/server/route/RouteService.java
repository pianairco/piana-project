package ir.piana.dev.webtool2.server.route;

import com.sun.research.ws.wadl.HTTPMethods;
import ir.piana.dev.webtool2.server.annotation.MethodHandler;
import ir.piana.dev.webtool2.server.annotation.PianaServer;
import ir.piana.dev.webtool2.server.asset.PianaAssetResolver;
import ir.piana.dev.webtool2.server.response.PianaResponse;
import ir.piana.dev.webtool2.server.session.Session;
import ir.piana.dev.webtool2.server.session.SessionManager;
import org.apache.log4j.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mohammad Rahmati, 5/3/2017 7:28 AM
 */
public class RouteService {
    protected Logger logger = Logger
            .getLogger(RouteService.class);
    protected static ExecutorService executorService =
            Executors.newFixedThreadPool(256);

    @Context
    protected Configuration config;
    protected PianaServer serverConfig = null;
    protected SessionManager sessionManager = null;
    protected Map<String, Method> methodMap =
            new LinkedHashMap<>();
    protected Map<String, PianaAssetResolver> assetMap =
            new LinkedHashMap<>();
    protected static PianaAssetResolver pianaAssetResolver = null;
    protected static final PianaResponse forbiddenPianaResponse =
            new PianaResponse(Status.FORBIDDEN, null);
    protected static final PianaResponse unauthorizedPianaResponse =
            new PianaResponse(Status.UNAUTHORIZED, null);
    protected static final PianaResponse notFoundPianaResponse =
            new PianaResponse(Status.NOT_FOUND, 0, "not found asset",
                    MediaType.TEXT_PLAIN);
    protected static final PianaResponse notAcceptablePianaResponse =
            new PianaResponse(Status.NOT_ACCEPTABLE, null);
    protected static final PianaResponse internalServerErrorPianaResponse =
            new PianaResponse(Status.INTERNAL_SERVER_ERROR, null);

    public RouteService() {
    }

    @PostConstruct
    protected void init()
            throws Exception {
        serverConfig = (PianaServer) config
                .getProperty("PIANA_SERVER_CONFIG");
        sessionManager = (SessionManager) config
                .getProperty(SessionManager
                        .PIANA_SESSION_MANAGER);
    }

    @MethodHandler(
            path = @Path("hello-world/{f-name}"),
            httpMethod = "GET")
    protected Map<String, List<String>> createParameters(
            UriInfo uriInfo) {
        Map<String, List<String>> collect = Stream.concat(
                uriInfo.getPathParameters().entrySet().stream(),
                uriInfo.getQueryParameters().entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue()
                ));
        return collect;
    }

    protected Map<String, List<String>> createParameters(
            Map<String, List<String>> parameters) {
        return parameters;
    }

    protected Map<String, List<String>> createParameters(
            Map<String, List<String>> parameters,
            String urlPattern,
            String methodPattern) {

        ArrayList<String> urlPatterns = new ArrayList<>();
        urlPatterns.add(urlPattern);
        parameters.put("url-pattern", urlPatterns);

        ArrayList<String> methodPatterns = new ArrayList<>();
        methodPatterns.add(methodPattern);
        parameters.put("method-pattern", methodPatterns);
        return parameters;
    }

    protected Map<String, List<String>> createParameters(
            UriInfo uriInfo,
            String urlPattern,
            String methodPattern) {
        Map<String, List<String>> collect = Stream.concat(
                uriInfo.getPathParameters().entrySet().stream(),
                uriInfo.getQueryParameters().entrySet().stream())
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue()
                ));
        ArrayList<String> urlPatterns = new ArrayList<>();
        urlPatterns.add(urlPattern);
        collect.put("url-pattern", urlPatterns);

        ArrayList<String> methodPatterns = new ArrayList<>();
        methodPatterns.add(methodPattern);
        collect.put("method-pattern", methodPatterns);
        return collect;
    }

    protected PianaAssetResolver registerAssetResolver(
            String urlPath, String assetPath) {
        PianaAssetResolver assetResolver = assetMap
                .get(urlPath);
        if(assetResolver == null) {
            assetResolver = PianaAssetResolver
                    .getInstance(assetPath);
            assetMap.put(urlPath, assetResolver);
            return assetResolver;
        }
        return assetResolver;
    }

    protected Method registerMethod(
            String urlPath,
            String callClassName,
            String callMethodName,
            Class<?>... paramList
    ) throws Exception {
        Method method = methodMap.get(urlPath);
        if(method == null) {
            Class c = Class.forName(callClassName);
            method = c.getDeclaredMethod(
                    callMethodName, paramList);
            methodMap.put(urlPath, method);
            return method;
        }
        return method;
    }

    protected Session doAuthorization(
            HttpHeaders httpHeaders) {
        return sessionManager
                .retrieveSession(httpHeaders);
    }

    protected Session doRevivalSession(
            HttpHeaders httpHeaders) {
        return sessionManager
                .revivalSession(httpHeaders);
    }

    protected PianaResponse invokeMethod(
            Method method,
            Object... parameters
    ) throws Exception {
        return (PianaResponse) method
                .invoke(null, parameters);
    }

    protected Response createResponse(
            PianaResponse pianaResponse,
            Session session,
            HttpHeaders httpHeaders) {
//        NewCookie sessionCookie = sessionManager
//                .makeSessionCookie(session);
        ResponseBuilder resBuilder = Response
                .status(
                        pianaResponse.getResponseStatus());
        if(pianaResponse.getStatus() != 0) {
            resBuilder.entity(new ResponseByStatus(
                    pianaResponse.getStatus(),
                    pianaResponse.getEntity()))
                    .header("Content-Type",
                            MediaType.APPLICATION_JSON
                                    .concat("; charset=")
                                    .concat(pianaResponse
                                            .getCharset()
                                            .displayName()));
        }
        else {
            resBuilder.entity(pianaResponse.getEntity())
                    .header("Content-Type",
                            pianaResponse.getMediaType()
                                    .concat("; charset=")
                                    .concat(pianaResponse
                                            .getCharset()
                                            .displayName()));
        }
        if(serverConfig.removeOtherCookies())
            resBuilder.cookie(sessionManager
                    .removeOtherCookies(
                            session, httpHeaders));
        else
            resBuilder.cookie(sessionManager
                    .makeSessionCookie(session));
        return resBuilder.build();
    }

    protected boolean isAssetExist(String rootPath,
                                   String relativePath) {
        File file = new File(rootPath, relativePath);
        return file.exists();
    }

    private static class ResponseByStatus {
        private int status;
        private Object entity;

        public ResponseByStatus(int status, Object entity) {
            this.status = status;
            this.entity = entity;
        }

        public int getStatus() {
            return status;
        }

        public Object getEntity() {
            return entity;
        }
    }
}
