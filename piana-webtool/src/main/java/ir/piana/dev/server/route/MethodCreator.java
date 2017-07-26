package ir.piana.dev.server.route;

import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import ir.piana.dev.server.role.RoleType;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by SYSTEM on 7/18/2017.
 */
public abstract class MethodCreator {
    final static Logger logger =
            Logger.getLogger(MethodCreator.class);
    protected final String urlPattern;
    protected final String methodPattern;
    protected final String httpMethod;
    protected PianaRouteConfig routeConfig;
    protected ForbiddenCheckable forbiddenCheckable;
    protected String methodKey;
    protected StringBuilder sb;
    protected String[] pathParamList;

    private MethodCreator(String urlPattern,
                          String methodPattern,
                          PianaRouteConfig routeConfig,
                          ForbiddenCheckable forbiddenCheckable) {
        this.urlPattern = urlPattern;
        this.methodPattern = methodPattern;
        this.routeConfig = routeConfig;
        this.forbiddenCheckable = forbiddenCheckable;
        this.httpMethod = fetchHttpMethod();
        this.pathParamList = fetchPathParamList();
        methodKey = urlPattern
                .concat(methodPattern);
        sb = new StringBuilder();
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

    public String createMethod()
            throws Exception {
        //@GET or @POST or @DELETE or ...
        sb.append("@".concat(httpMethod).concat("\n"));

        //@Path({path-param}/{path-param})
        if(pathParamList != null) {
            sb.append("@Path(\""
                    .concat(createRestPathURL())
                    .concat("\")\n"));
        }

        //@Consumes(MediaType.*)
        String consumes = createConsumes();
        if(consumes != null)
            sb.append(consumes);

        sb.append(getMethodSignature());

        //open method body
        sb.append("{\n");

        RoleType routeRoleType = RoleType.ADMIN;
        try {
            routeRoleType = RoleType
                    .getFromName(routeConfig.getRole());
        } catch (Exception e) {
            logger.error("SyncHandler:".concat(e.getMessage()));
        }

        sb.append(getMethodBody(routeRoleType));

        //close method body
        sb.append("}\n");

        return sb.toString();
    }

    protected abstract String getMethodSignature() throws Exception;

    protected abstract String getMethodBody(RoleType routeRoleType) throws Exception;

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
        if(!methodPattern.contains("#"))
            return methodPattern;
        return methodPattern.substring(0,
                methodPattern.indexOf("#"));
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
        if(methodPattern.contains("#"))
            return methodPattern.substring(
                    methodPattern.indexOf("#") + 1,
                    methodPattern.length());
        return null;
    }

    private String createRestPathURL() {
        String path = "";
        for(String pathParam :
                pathParamList)
            path = path.concat("/{")
                    .concat(pathParam).concat("}");
        return path;
    }

    private String createConsumes()
            throws Exception {
        String bodyJsonObject =
                routeConfig.getBodyJsonObject();
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            if(httpMethod.equalsIgnoreCase("GET") ||
                    httpMethod.equalsIgnoreCase("HEAD") ||
                    httpMethod.equalsIgnoreCase("DELETE") ||
                    httpMethod.equalsIgnoreCase("TRACE") ||
                    httpMethod.equalsIgnoreCase("OPTIONS") ||
                    httpMethod.equalsIgnoreCase("CONNECT"))
                throw new Exception("http method don't support body");
            return "@Consumes(MediaType.APPLICATION_JSON)\n";
        }
        return null;
    }

    protected String getMethodName()
            throws Exception {
        if(methodPattern == null)
            throw new Exception("method pattern is null.");
        String methodName = methodPattern;
        if(methodName.startsWith("GET"))
            methodName = methodName.replace("GET", "get");
        else if(methodName.startsWith("POST"))
            methodName = methodName.replace("POST", "post");
        else if(methodName.startsWith("PUT"))
            methodName = methodName.replace("PUT", "put");
        else if(methodName.startsWith("DELETE"))
            methodName = methodName.replace("DELETE", "delete");
        else if(methodName.startsWith("OPTIONS"))
            methodName = methodName.replace("OPTIONS", "options");
        else if(methodName.startsWith("HEAD"))
            methodName = methodName.replace("HEAD", "head");
        else
            throw new Exception("method pattern is incorrect.");

        if(!methodName .contains("#"))
            return methodName.concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            8, SecureRandomType.SHA_1_PRNG)));

        char[] chars = methodName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '#' || chars[i] == ':') {
                chars[++i] = String.valueOf(chars[i])
                        .toUpperCase().charAt(0);
            }
        }
        return new String(chars)
                .replaceAll("#", "")
                .replaceAll(":", "")
                .replaceAll("\\*", "");
    }

    protected String fetchConsumeObjectName() {
        String bodyJsonObject =
                routeConfig.getBodyJsonObject();
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            return bodyJsonObject.substring(
                    bodyJsonObject
                            .lastIndexOf(".") + 1);
        }
        return null;
    }

    protected String createBodyObjectParam() {
        String bodyJsonObject = routeConfig
                .getBodyJsonObject();
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            return bodyJsonObject
                    .concat(" ")
                    .concat(fetchConsumeObjectName());
        }
        return null;
    }

    protected String getSyncMethodSignature()
            throws Exception {
        StringBuilder signature = new StringBuilder();
        String methodName = getMethodName();
        signature.append("public Response "
                .concat(methodName)
                .concat("(@Context HttpHeaders httpHeaders,")
                .concat("@Context UriInfo uriInfo"));

        //add body object to parameter list
        String bodyObjectParam = createBodyObjectParam();
        if(bodyObjectParam != null) {
            signature = signature.append(",")
                    .append(bodyObjectParam);
        }

        return signature.append(") throws Exception ").toString();
    }

    protected String getAsyncMethodSignature()
            throws Exception {
        StringBuilder signature = new StringBuilder();
        String methodName = getMethodName();
        signature.append("public void "
                .concat(methodName)
                .concat("(@Suspended final AsyncResponse asyncResponse,")
                .concat("@Context HttpHeaders httpHeaders,")
                .concat("@Context UriInfo uriInfo"));

        //add body object to parameter list
        String bodyObjectParam = createBodyObjectParam();
        if(bodyObjectParam != null) {
            signature = signature.append(",")
                    .append(bodyObjectParam);
        }

        return signature.append(") throws Exception ").toString();
    }

    protected String fetchCallClassName()
            throws Exception {
        String handler = routeConfig.getHandler();
        String callClassName = null;
        if(handler != null && !handler.isEmpty()) {
            callClassName = handler.substring(
                    0, handler.lastIndexOf("."));
        }
        if(callClassName == null
                && !routeConfig.isAsset()) {
            throw new Exception("handler is incorrect.");
        }
        return callClassName;
    }

    protected String fetchCallMethodName()
            throws Exception {
        String handler = routeConfig.getHandler();
        String callMethodName = null;
        if(handler != null && !handler.isEmpty()) {
            callMethodName = handler.substring(
                    handler.lastIndexOf(".") + 1);
        }
        if(callMethodName == null
                && !routeConfig.isAsset()) {
            throw new Exception("handler is incorrect.");
        }
        return callMethodName;
    }

    // -------------------------- factory --------------------------------

    private interface ForbiddenCheckable {
        String checkForbidden(RoleType roleType);
    }

    public static MethodCreator getInstance(String urlPattern,
                                            String methodPattern,
                                            PianaRouteConfig routeConfig)
            throws Exception {
        MethodCreator methodCreator = null;

        boolean isAsset = isAssetMethod(routeConfig);
        boolean isAsync = routeConfig.isAsync();

        if(isAsync && isAsset)
            methodCreator = new ASyncAsset(urlPattern, methodPattern, routeConfig, MethodCreator::checkForbiddenForAsync);
        else if(isAsync && !isAsset)
            methodCreator = new ASyncHandler(urlPattern, methodPattern, routeConfig, MethodCreator::checkForbiddenForAsync);
        else if(!isAsync && isAsset)
            methodCreator = new SyncAsset(urlPattern, methodPattern, routeConfig, MethodCreator::checkForbiddenForSync);
        else if(!isAsync && !isAsset)
            methodCreator = new SyncHandler(urlPattern, methodPattern, routeConfig, MethodCreator::checkForbiddenForSync);

        return methodCreator;
    }

    // -------------------------- sub classes ----------------------------

    private static class SyncHandler extends MethodCreator {
        private SyncHandler(String urlPattern,
                            String methodPattern,
                            PianaRouteConfig routeConfig,
                            ForbiddenCheckable forbiddenCheckable) {
            super(urlPattern, methodPattern, routeConfig, forbiddenCheckable);
        }

        @Override
        protected String getMethodSignature()
                throws Exception {
            return getSyncMethodSignature();
        }

        @Override
        protected String getMethodBody(RoleType routeRoleType)
                throws Exception {
            StringBuilder sb = new StringBuilder(forbiddenCheckable.checkForbidden(routeRoleType));
//            else
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");

            sb.append("try {\n");
            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("if(!RoleType."
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("return createResponse(unauthorizedPianaResponse, session, httpHeaders);\n"));
            }
            ///start of register method
            String registerMethod = "Method m = registerMethod(\""
                    .concat(methodKey)
                    .concat("\",");

            String callClassName = fetchCallClassName();
            String callMethodName = fetchCallMethodName();

            registerMethod = registerMethod.concat("\"")
                    .concat(callClassName)
                    .concat("\",\"")
                    .concat(callMethodName)
                    .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");
            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");

            sb.append("return createResponse(invokeMethod(m, session,");

            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = fetchConsumeObjectName();
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat("),"));
            } else {
                sb.deleteCharAt(sb.length() - 1);
                sb.append("),");
            }

//            sb.deleteCharAt(sb.length() - 1);
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

    private static class ASyncHandler extends MethodCreator {
        private ASyncHandler(String urlPattern,
                             String methodPattern,
                             PianaRouteConfig routeConfig,
                             ForbiddenCheckable forbiddenCheckable) {
            super(urlPattern, methodPattern, routeConfig, forbiddenCheckable);
        }

        @Override
        protected String getMethodSignature()
                throws Exception {
            return getAsyncMethodSignature();
        }

        @Override
        protected String getMethodBody(RoleType routeRoleType)
                throws Exception {
            StringBuilder sb = new StringBuilder(forbiddenCheckable.checkForbidden(routeRoleType));
            sb.append("try {\n");

            ///start of registerMethod
            String registerMethod = "Method m = registerMethod(\""
                    .concat(methodKey)
                    .concat("\",");

            String callClassName = fetchCallClassName();
            String callMethodName = fetchCallMethodName();

            registerMethod = registerMethod.concat("\"")
                    .concat(callClassName)
                    .concat("\",\"")
                    .concat(callMethodName)
                    .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");

            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");
            ///end of register method

            sb.append("executorService.execute(() -> {\n")
                    .append("try {\n");

            ///if resource have role type
            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("if(!RoleType."
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("asyncResponse.resume(createResponse(unauthorizedPianaResponse, session, httpHeaders));\n"));
            }
//            else
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");

            sb.append("PianaResponse response = invokeMethod(m, session,");
            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = fetchConsumeObjectName();
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat(","));
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");\n");
            sb.append("asyncResponse.resume(createResponse(response, session, httpHeaders));\n");
            ///end of invokeMethod

            String excName = "exc_".concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            16, SecureRandomType.SHA_1_PRNG)));
            sb.append("} catch (Exception ".concat(excName).concat(") {\n"));
            sb.append("logger.error(".concat(excName).concat(");\n"));
            sb.append("asyncResponse.resume(createResponse(internalServerErrorPianaResponse, session, httpHeaders));\n");
            sb.append("}\n});\n");
            ///end of executorService

            String excName2 = "exc_".concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            16, SecureRandomType.SHA_1_PRNG)));
            sb.append("} catch (Exception ".concat(excName2).concat(") {\n"));
            sb.append("logger.error(".concat(excName2).concat(");\n"));
            sb.append("asyncResponse.resume(createResponse(internalServerErrorPianaResponse, session, httpHeaders));\n");
            sb.append("}\n");

            return sb.toString();
        }
    }

    private static class SyncAsset extends MethodCreator {
        private SyncAsset(String urlPattern,
                          String methodPattern,
                          PianaRouteConfig routeConfig,
                          ForbiddenCheckable forbiddenCheckable) {
            super(urlPattern, methodPattern, routeConfig, forbiddenCheckable);
        }

        @Override
        protected String getMethodSignature()
                throws Exception {
            return getSyncMethodSignature();
        }

        @Override
        protected String getMethodBody(RoleType routeRoleType)
                throws Exception {
            StringBuilder sb = new StringBuilder(forbiddenCheckable.checkForbidden(routeRoleType));

            if(pathParamList != null
                    && pathParamList.length == 1) {
                sb.append("if(!isAssetExist(\""
                        .concat(routeConfig.getAssetPath())
                        .concat("\",")
                        .concat("uriInfo.getPathParameters().getFirst(\"")
                        .concat(pathParamList[0].substring(
                                0, pathParamList[0].indexOf(':')))
                        .concat("\")")
                        .concat(")) {\n")
                        .concat("return createResponse(notFoundPianaResponse, null, httpHeaders);\n")
                        .concat("}\n"));
            } else {
                sb.append("if(!isAssetExist(\""
                        .concat(routeConfig.getAssetPath())
                        .concat("\",")
                        .concat("\"index.html\")")
                        .concat(") {\n")
                        .concat("return createResponse(notFoundPianaResponse, null, httpHeaders);\n")
                        .concat("}\n"));
            }
//            else
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");

            sb.append("try {\n");
            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("if(!RoleType."
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("return createResponse(unauthorizedPianaResponse, session, httpHeaders);\n"));
            }
            sb.append("PianaAssetResolver assetResolver = "
                    .concat("registerAssetResolver(\"")
                    .concat(methodKey)
                    .concat("\",\"")
                    .concat(routeConfig.getAssetPath())
                    .concat("\");\n"));

            ///start of register method
            String registerMethod = "Method m = registerMethod(\""
                    .concat(methodKey)
                    .concat("\",");

            String callClassName = fetchCallClassName();
            String callMethodName = fetchCallMethodName();

            if(callClassName == null || callMethodName == null)
                registerMethod = registerMethod.concat(
                        "\"ir.piana.dev.server.route.AssetService\",")
                        .concat("\"getAsset\",");
            else
                registerMethod = registerMethod.concat("\"")
                        .concat(callClassName)
                        .concat("\",\"")
                        .concat(callMethodName)
                        .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");
            registerMethod = registerMethod
                    .concat("PianaAssetResolver.class,");
            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");
            sb.append("return createResponse(invokeMethod(m, session, assetResolver,");
            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = fetchConsumeObjectName();
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat("),"));
            } else {
                sb.deleteCharAt(sb.length() - 1);
                sb.append("),");
            }
//            sb.deleteCharAt(sb.length() - 1);
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

    private static class ASyncAsset extends MethodCreator {
        private ASyncAsset(String urlPattern,
                           String methodPattern,
                           PianaRouteConfig routeConfig,
                           ForbiddenCheckable forbiddenCheckable) {
            super(urlPattern, methodPattern, routeConfig, forbiddenCheckable);
        }

        @Override
        protected String getMethodSignature()
                throws Exception {
            return getAsyncMethodSignature();
        }

        @Override
        protected String getMethodBody(RoleType routeRoleType)
                throws Exception {
            StringBuilder sb = new StringBuilder(forbiddenCheckable.checkForbidden(routeRoleType));

            sb.append("try {\n");

            sb.append("PianaAssetResolver assetResolver = "
                    .concat("registerAssetResolver(\"")
                    .concat(methodKey)
                    .concat("\",\"")
                    .concat(routeConfig.getAssetPath())
                    .concat("\");\n"));

            ///start of registerMethod
            String registerMethod = "Method m = registerMethod(\""
                    .concat(methodKey)
                    .concat("\",");

            String callClassName = fetchCallClassName();
            String callMethodName = fetchCallMethodName();

            if(callClassName == null || callMethodName == null)
                registerMethod = registerMethod.concat(
                        "\"ir.piana.dev.server.route.AssetService\",")
                        .concat("\"getAsset\",");
            else
                registerMethod = registerMethod.concat("\"")
                        .concat(callClassName)
                        .concat("\",\"")
                        .concat(callMethodName)
                        .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");
            registerMethod = registerMethod
                    .concat("PianaAssetResolver.class,");
            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");
            ///end of register method

            sb.append("executorService.execute(() -> {\n")
                    .append("try {\n");

            if(pathParamList != null
                    && pathParamList.length == 1) {
                sb.append("if(!isAssetExist(\""
                        .concat(routeConfig.getAssetPath())
                        .concat("\",")
                        .concat("uriInfo.getPathParameters().getFirst(\"")
                        .concat(pathParamList[0].substring(
                                0, pathParamList[0].indexOf(':')))
                        .concat("\")")
                        .concat(")) {\n")
                        .concat("asyncResponse.resume(createResponse(notFoundPianaResponse, null, httpHeaders));\n")
                        .concat("return;\n")
                        .concat("}\n"));
            } else {
                sb.append("if(!isAssetExist(\""
                        .concat(routeConfig.getAssetPath())
                        .concat("\",")
                        .concat("\"index.html\")")
                        .concat(") {\n")
                        .concat("asyncResponse.resume(createResponse(notFoundPianaResponse, null, httpHeaders));\n")
                        .concat("return;\n")
                        .concat("}\n"));
            }

            ///if resource have role type
            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("if(!RoleType."
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("asyncResponse.resume(createResponse(unauthorizedPianaResponse, session, httpHeaders));\n"));
            }
//            else
//                sb.append("final Session session = doRevivalSession(httpHeaders);\n");

            sb.append(
                    "PianaResponse response = invokeMethod(m, session, assetResolver,");

            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = fetchConsumeObjectName();
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat(","));
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");\n");
            sb.append("asyncResponse.resume(createResponse(response, session, httpHeaders));\n");
            ///end of invokeMethod

            String excName = "exc_".concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            16, SecureRandomType.SHA_1_PRNG)));
            sb.append("} catch (Exception ".concat(excName).concat(") {\n"));
            sb.append("logger.error(".concat(excName).concat(");\n"));
            sb.append("asyncResponse.resume(createResponse(internalServerErrorPianaResponse, session, httpHeaders));\n");
            sb.append("}\n});\n");
            ///end of executorService

            String excName2 = "exc_".concat(HexConverter.toHexString(
                    SecureRandomMaker.makeByteArray(
                            16, SecureRandomType.SHA_1_PRNG)));
            sb.append("} catch (Exception ".concat(excName2).concat(") {\n"));
            sb.append("logger.error(".concat(excName2).concat(");\n"));
            sb.append("asyncResponse.resume(createResponse(internalServerErrorPianaResponse, session, httpHeaders));\n");
            sb.append("}\n");

            return sb.toString();
        }
    }

}

