package ir.piana.dev.webtool2.server.route;

import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import ir.piana.dev.server.role.RoleType;
import ir.piana.dev.webtool2.server.annotation.AnnotationController;
import ir.piana.dev.webtool2.server.annotation.AssetHandler;
import ir.piana.dev.webtool2.server.annotation.Handler;
import ir.piana.dev.webtool2.server.annotation.MethodHandler;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by SYSTEM on 7/18/2017.
 */
public abstract class HandlerMethodCreator {
    final static Logger logger =
            Logger.getLogger(HandlerMethodCreator.class);
    protected String methodKey;
    protected ForbiddenCheckable forbiddenCheckable;

    private HandlerMethodCreator(ForbiddenCheckable forbiddenCheckable) {
        this.forbiddenCheckable = forbiddenCheckable;
    }

    protected abstract String makeMethod() throws Exception;
    protected abstract String makeMethodAnnotation();
    protected abstract String makeMethodSignature();
    protected abstract String makeForbiddenChecker();
    protected abstract String makeRestOfBody() throws Exception;

    protected String makeMethodEnd() {
        return "}\n";
    }

    protected String makeMethodBody() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(makeMethodAnnotation());
        sb.append(makeMethodSignature());
        sb.append(makeForbiddenChecker());
        sb.append(makeRestOfBody());
        sb.append(makeMethodEnd());
        return sb.toString();
    }

    public String create()
            throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(makeMethod());
        return sb.toString();
    }

    // ---------------------- utility classes ---------------------------

    private static boolean isAssetMethod(PianaRouteConfig routeConfig)
            throws Exception {
        boolean isAsset = routeConfig.isAsset();
        if(routeConfig.getPathParams() != null) {
            if(isAsset &&
                    routeConfig.getPathParams().size() > 1) {
                throw new Exception(
                        "this asset path not set corrected!");
            }
        }
        return isAsset;
    }

    private String fetchHttpMethod() {
//        if(!methodPattern.contains("#"))
//            return methodPattern;
//        return methodPattern.substring(0,
//                methodPattern.indexOf("#"));
        return null;
    }

    private String[] fetchPathParamList() {
        String pathParamStrings =
                fetchPathParamStrings();
        if(pathParamStrings == null)
            return null;
        String[] params = pathParamStrings.split(":");
        String[] outParams = new String[params.length];
        for(int i = 0; i < params.length; i++) {
            String temp = params[i];
            if(temp.contains("*"))
                outParams[i] = temp.substring(0, temp.indexOf('*')).concat(":.*");
            else
                outParams[i] = temp;
        }
        return outParams;
    }

    private String fetchPathParamStrings() {
//        if(methodPattern.contains("#"))
//            return methodPattern.substring(
//                    methodPattern.indexOf("#") + 1,
//                    methodPattern.length());
        return null;
    }


    protected String getMethodName()
            throws Exception {
//        if(methodPattern == null)
//            throw new Exception("method pattern is null.");
//        String methodName = methodPattern;
//        if(methodName.startsWith("GET"))
//            methodName = methodName.replace("GET", "get");
//        else if(methodName.startsWith("POST"))
//            methodName = methodName.replace("POST", "post");
//        else if(methodName.startsWith("PUT"))
//            methodName = methodName.replace("PUT", "put");
//        else if(methodName.startsWith("DELETE"))
//            methodName = methodName.replace("DELETE", "delete");
//        else if(methodName.startsWith("OPTIONS"))
//            methodName = methodName.replace("OPTIONS", "options");
//        else if(methodName.startsWith("HEAD"))
//            methodName = methodName.replace("HEAD", "head");
//        else
//            throw new Exception("method pattern is incorrect.");
//
//        if(!methodName .contains("#"))
//            return methodName.concat(HexConverter.toHexString(
//                    SecureRandomMaker.makeByteArray(
//                            8, SecureRandomType.SHA_1_PRNG)));
//
//        char[] chars = methodName.toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            if(chars[i] == '#' || chars[i] == ':') {
//                chars[++i] = String.valueOf(chars[i])
//                        .toUpperCase().charAt(0);
//            }
//        }
//        return new String(chars)
//                .replaceAll("#", "")
//                .replaceAll(":", "")
//                .replaceAll("\\*", "");
        return null;
    }

    protected static String checkForbiddenForSync(RoleType routeRoleType) {
        StringBuilder sb = new StringBuilder();
        if (routeRoleType != RoleType.NEEDLESS) {
            sb.append("final Session session = doAuthorization(httpHeaders);\n");
            sb.append("if(session != null && session.isWrongdoer())\n"
                    .concat("return createResponse(forbiddenPianaResponse, session, httpHeaders);\n"));
        } else {
            sb.append("final Session session = doRevivalSession(httpHeaders);\n");
            sb.append("if(session != null && session.isWrongdoer())\n"
                    .concat("return createResponse(forbiddenPianaResponse, session, httpHeaders);\n"));
        }
        return sb.toString();
    }

    protected static String checkForbiddenForAsync(RoleType routeRoleType) {
        StringBuilder sb = new StringBuilder();
        if (routeRoleType != RoleType.NEEDLESS) {
            sb.append("final Session session = doAuthorization(httpHeaders);\n");
            sb.append("if(session != null && session.isWrongdoer()) {\n"
                    .concat("asyncResponse.resume(createResponse(forbiddenPianaResponse, session, httpHeaders));\n")
                    .concat("return;\n")
                    .concat("}\n"));
        } else {
            sb.append("final Session session = doRevivalSession(httpHeaders);\n");
            sb.append("if(session != null && session.isWrongdoer()) {\n"
                    .concat("asyncResponse.resume(createResponse(forbiddenPianaResponse, session, httpHeaders));\n")
                    .concat("return;\n")
                    .concat("}\n"));
        }
        return sb.toString();
    }

    // -------------------------- factory --------------------------------

    private interface ForbiddenCheckable {
        String checkForbidden(RoleType roleType);
    }

    public static HandlerMethodCreator getAssetHandlerInstance(
            Class targetClass)
            throws Exception {
        AssetHandler assetHandler = AnnotationController
                .getAssetHandler(targetClass);
        HandlerMethodCreator methodCreator = null;
        boolean isSync = assetHandler.isSync();
        String methodKey = "GET#"
                .concat(AnnotationController.getHandler(targetClass).baseUrl())
                .concat("File");
        if(isSync)
            methodCreator = new SyncAssetHandlerMethodCreator(
                    assetHandler, HandlerMethodCreator::checkForbiddenForSync);
        else
            methodCreator = new AsyncAssetHandlerMethodCreator(
                    assetHandler, HandlerMethodCreator::checkForbiddenForAsync);
        return methodCreator;
    }

    public static HandlerMethodCreator getMethodHandlerInstance(
            Method targetMethod)
            throws Exception {
        MethodHandler methodHandler = AnnotationController
                .getMethodHandler(targetMethod);
        boolean isSync = methodHandler.isSync();

        HandlerMethodCreator methodCreator = null;
        if(isSync)
            methodCreator = new SyncMethodHandlerMethodCreator(
                    targetMethod, methodHandler, HandlerMethodCreator::checkForbiddenForSync);
        else
            methodCreator = new ASyncMethodHandlerMethodCreator(
                    targetMethod, methodHandler, HandlerMethodCreator::checkForbiddenForAsync);

        return methodCreator;
    }

    // -------------------------- sub classes ----------------------------

    private abstract static class  AssetHandlerMethodCreator
            extends HandlerMethodCreator {
        protected AssetHandler assetHandler;

        private AssetHandlerMethodCreator (
                AssetHandler assetHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(forbiddenCheckable);
            this.assetHandler = assetHandler;
        }

        public String makeMethod()
                throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append(makeMethodBody());

            sb.append(makeMethodAnnotationWithoutPath());
            sb.append(makeMethodSignatureWithoutPath());
            sb.append(makeForbiddenChecker());
            sb.append(makeRestOfBody());
            sb.append(makeMethodEnd());
            return sb.toString();
        }

        @Override
        protected String makeMethodAnnotation() {
            StringBuilder sb = new StringBuilder();
            sb.append("@GET\n");
            sb.append("@Path(\"/{file:.*}\")\n");
            return sb.toString();
        }

        protected abstract String makeMethodSignatureWithoutPath();

        protected String makeMethodAnnotationWithoutPath() {
            StringBuilder sb = new StringBuilder("@GET\n");
            return sb.toString();
        }

        @Override
        protected String makeForbiddenChecker() {
            StringBuilder sb = new StringBuilder();
            sb.append(forbiddenCheckable.checkForbidden(assetHandler.roleType()));
//            if (assetHandler.roleType() != RoleType.NEEDLESS) {
//                sb.append("final Session session = doAuthorization(httpHeaders);\n");
//                sb.append("if(session != null && session.isWrongdoer())\n"
//                        .concat("return createResponse(forbiddenPianaResponse, session, httpHeaders);\n"));
//            } else {
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");
//                sb.append("if(session != null && session.isWrongdoer())\n"
//                        .concat("return createResponse(forbiddenPianaResponse, session, httpHeaders);\n"));
//            }
            return sb.toString();
        }
    }

    private static class SyncAssetHandlerMethodCreator
            extends AssetHandlerMethodCreator {
        private SyncAssetHandlerMethodCreator(
                AssetHandler assetHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(assetHandler, forbiddenCheckable);
        }

        @Override
        protected String makeMethodSignature() {
            StringBuilder sb = new StringBuilder();
            sb.append("public Response getFile(@Context HttpHeaders httpHeaders,"
                    .concat("@Context UriInfo uriInfo,")
                    .concat("@PathParam(\"file\") String file)")
                    .concat("throws Exception {\n"));
            return sb.toString();
        }

        @Override
        protected String makeMethodSignatureWithoutPath(){
            StringBuilder sb = new StringBuilder();
            sb.append("public Response getFileWithoutParam(@Context HttpHeaders httpHeaders,"
                    .concat("@Context UriInfo uriInfo)")
                    .concat("throws Exception {\nString file = null;\n"));
            return sb.toString();
        }

        @Override
        protected String makeRestOfBody()
                throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append("if(file == null || file.isEmpty()) {\n");
            sb.append("if(!isAssetExist(\""
                    .concat(assetHandler.assetPath()).concat("\", \"index.html\"))\n")
                    .concat("return createResponse(notFoundPianaResponse, session, httpHeaders);\n")
            );
            sb.append("} else {\n");
            sb.append("if(!isAssetExist(\""
                    .concat(assetHandler.assetPath())
                    .concat("\",file))\n")
                    .concat("return createResponse(notFoundPianaResponse, session, httpHeaders);\n")
                    .concat("}\n")
            );

            sb.append("try {\n");
            if (assetHandler.roleType() != RoleType.NEEDLESS) {
                sb.append("if(!RoleType."
                        .concat(assetHandler.roleType().getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("return createResponse(unauthorizedPianaResponse, session, httpHeaders);\n"));
            }

            ///start of register method
            String registerMethod = "Method m = registerMethod(\""
                    .concat("getFile")
                    .concat("\",");

            registerMethod = registerMethod.concat(
                    "\"ir.piana.dev.webtool2.server.route.AssetService\",")
                    .concat("\"getAsset\",");
            registerMethod = registerMethod.concat("Session.class,");
            registerMethod = registerMethod
                    .concat("PianaAssetResolver.class,");
            registerMethod = registerMethod.concat(
                    "String.class");
            sb.append(registerMethod);
            sb.append(");\n");
            sb.append("return createResponse(invokeMethod(m, session, assetResolver, file),");
//            if(assetHandler.urlInjected())
//                sb.append("createParameters(uriInfo,\""
//                        .concat(urlPattern)
//                        .concat("\",\"")
//                        .concat(methodPattern)
//                        .concat("\"),"));
//            else
//                sb.append("createParameters(uriInfo),");
            sb.append(" session, httpHeaders);\n");

            ///end of invokeMethod
            String excName = "exc_".concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            16, SecureRandomType.SHA_1_PRNG)));
            sb.append("} catch (Exception ".concat(excName).concat(") {\n"));
            sb.append("System.out.println(".concat(excName).concat(".getMessage());\n"));
            sb.append("logger.error(".concat(excName).concat(");\n"));
            sb.append("return createResponse(internalServerErrorPianaResponse, session, httpHeaders);\n}\n");
            return sb.toString();
        }
    }

    private static class AsyncAssetHandlerMethodCreator
            extends AssetHandlerMethodCreator {
        private AsyncAssetHandlerMethodCreator(
                AssetHandler assetHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(assetHandler, forbiddenCheckable);
        }

        @Override
        protected String makeMethodSignatureWithoutPath() {
            return null;
        }

        @Override
        protected String makeMethodSignature() {
            return null;
        }

        @Override
        protected String makeRestOfBody() {
            return null;
        }
    }

    //----------------------------------------------------------------------

    private static abstract class  MethodHandlerMethodCreator
            extends HandlerMethodCreator {
        protected MethodHandler methodHandler;
        protected Method method;
        private MethodHandlerMethodCreator (
                Method method, MethodHandler methodHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(forbiddenCheckable);
            this.method = method;
            this.methodHandler = methodHandler;
        }

        public String makeMethod()
                throws Exception {
            StringBuilder sb = new StringBuilder();
            sb.append(makeMethodBody());
            return sb.toString();
        }

        @Override
        protected String makeForbiddenChecker() {
            StringBuilder sb = new StringBuilder();
            sb.append(forbiddenCheckable.checkForbidden(methodHandler.roleType()));
//            if (methodHandler.roleType() != RoleType.NEEDLESS) {
//                sb.append("final Session session = doAuthorization(httpHeaders);\n");
//                sb.append("if(session != null && session.isWrongdoer()) {\n"
//                        .concat("asyncResponse.resume(createResponse(forbiddenPianaResponse, session, httpHeaders));\n")
//                        .concat("return;\n")
//                        .concat("}\n"));
//            } else {
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");
//                sb.append("if(session != null && session.isWrongdoer()) {\n"
//                        .concat("asyncResponse.resume(createResponse(forbiddenPianaResponse, session, httpHeaders));\n")
//                        .concat("return;\n")
//                        .concat("}\n"));
//            }
            return sb.toString();
        }
    }

    private static class SyncMethodHandlerMethodCreator
            extends MethodHandlerMethodCreator {
        private SyncMethodHandlerMethodCreator(
                Method method, MethodHandler methodHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(method, methodHandler, forbiddenCheckable);
        }

        @Override
        protected String makeMethodAnnotation() {
            StringBuilder sb = new StringBuilder();
            sb.append("@".concat(methodHandler
                    .httpMethod()).concat("\n"));
            Path annotation = method.getAnnotation(Path.class);
            sb.append("@Path(\"".concat(annotation == null? "" : annotation.value()).concat("\")\n"));
            Consumes consumes = method.getAnnotation(Consumes.class);
            if(consumes != null &&
                    methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.POST) ||
                    methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.PUT))
                sb.append("@Consumes(MediaType.APPLICATION_JSON)\n");
            return sb.toString();
        }

        @Override
        protected String makeMethodSignature() {
            StringBuilder sb = new StringBuilder();
            sb.append("public Response ".concat(method.getName())
                    .concat("(@Context HttpHeaders httpHeaders,@Context UriInfo uriInfo,"));
            Parameter[] parameters = method.getParameters();
            Consumes consumes = null;
            for (int i = 1; i < parameters.length; i++) {
                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
                if(pathParam != null)
                    sb.append("@PathParam(\"".concat(pathParam.value())
                            .concat("\")").concat(parameters[i].toString()).concat(","));
                else if(queryParam != null) {
                    if (pathParam != null)
                        sb.append("@QueryParam(\"".concat(queryParam.value())
                                .concat("\")").concat(parameters[i].toString()).concat(","));
                }
                else if(consumes == null &&
                        methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.POST) ||
                        methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.PUT)) {
                    consumes = method.getAnnotation(Consumes.class);
                    if(consumes != null) {
                        if(consumes.value().length == 1 &&
                                consumes.value()[0].equalsIgnoreCase(MediaType.APPLICATION_JSON))
                            sb.append(parameters[i].toString().concat(","));
                    }
                }
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append(") throws Exception {\n");
            return sb.toString();
        }

        @Override
        protected String makeRestOfBody() {
            StringBuilder sb = new StringBuilder();
            sb.append("if(session != null && !RoleType.GUEST.isValid(session.getRoleType()))\n");
            sb.append("return createResponse(unauthorizedPianaResponse, session, httpHeaders);\n");
            sb.append("try {\n");
            sb.append("Method m = registerMethod(\""
                    .concat(method.getName())
                    .concat("\",\"").concat(method.getDeclaringClass().getName()).concat("\",\"")
                    .concat(method.getName()).concat("\",Session.class,"));
            Parameter[] parameters = method.getParameters();
            Consumes consumes = null;
            for (int i = 1; i < parameters.length; i++) {
                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
                if(pathParam != null || queryParam != null) {
                    sb.append(parameters[i].getType().getName().concat(".class,"));
                }
                else if(consumes == null &&
                        methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.POST) ||
                        methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.PUT)) {
                    consumes = method.getAnnotation(Consumes.class);
                    if (consumes != null)
                        sb.append(parameters[i].getType().getName().concat(".class,"));
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");\n");
            sb.append("return createResponse(invokeMethod(m, session,");
            consumes = null;
            for (int i = 1; i < parameters.length; i++) {
                PathParam pathParam = parameters[i].getAnnotation(PathParam.class);
                QueryParam queryParam = parameters[i].getAnnotation(QueryParam.class);
                if(pathParam != null || queryParam != null) {
                    sb.append(parameters[i].getName().concat(","));
                } else if(consumes == null &&
                            methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.POST) ||
                            methodHandler.httpMethod().equalsIgnoreCase(HttpMethod.PUT)) {
                    consumes = method.getAnnotation(Consumes.class);
                    if(consumes != null)
                        sb.append(parameters[i].getName().concat(","));
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("), session, httpHeaders);\n");
            sb.append("} catch (Exception exc_9b6bd4a8b95b5e820f471442ab506d75) {\n");
            sb.append("logger.error(this.getClass().getName() + \" : \" + exc_9b6bd4a8b95b5e820f471442ab506d75);\n");
            sb.append("return createResponse(internalServerErrorPianaResponse, session, httpHeaders);\n");
            sb.append("}\n");
            return sb.toString();
        }
    }

    private static class ASyncMethodHandlerMethodCreator
            extends MethodHandlerMethodCreator {
        private ASyncMethodHandlerMethodCreator(
                Method method, MethodHandler methodHandler,
                ForbiddenCheckable forbiddenCheckable) {
            super(method, methodHandler, forbiddenCheckable);
        }

        @Override
        protected String makeMethodAnnotation() {
            return null;
        }

        @Override
        protected String makeMethodSignature() {
            return null;
        }

        @Override
        protected String makeRestOfBody() {
            return null;
        }
    }
}

