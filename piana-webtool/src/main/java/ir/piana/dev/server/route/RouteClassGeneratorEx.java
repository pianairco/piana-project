package ir.piana.dev.server.route;

import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import ir.piana.dev.server.role.RoleType;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static io.netty.util.internal.SystemPropertyUtil.contains;
import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

/**
 * @author Mohammad Rahmati, 5/7/2017 5:20 PM
 */
public class RouteClassGeneratorEx {
    final static Logger logger =
            Logger.getLogger(RouteClassGeneratorEx.class);
    private static final String packageName =
            "ir.piana.dev.server.route";

    public static Set<Class<?>> generateRouteClasses(
            PianaRouterConfig routerConfig,
            String outputClassPath)
            throws Exception {
        Set<String> urlPattens = routerConfig.getUrlPattens();
        Set<Class<?>> classes = new HashSet<>();

        boolean setRoot = false;
        for(String urlPattern : urlPattens) {
            final String className = getClassName(urlPattern);
            final String fullClassName = packageName
                    .replace('.', '/')
                    .concat("/")
                    .concat(className);
            StringBuilder classSource = createClassSource(
                    routerConfig, urlPattern, className);

            if(outputClassPath != null && !outputClassPath.isEmpty()) {

                File directory = new File(outputClassPath);
                try {
                    if (!directory.exists()) {
                        /**
                         * If you require it to make the
                         * entire directory path including parents
                         * use of directory.mkdirs();
                         * else, use of directory.mkdir();*/
                        directory.mkdirs();
                    }
                    writeClassToFile(directory.getPath(),
                            className, classSource);
                } catch (Exception e) {
                    logger.error("not can make class file " +
                            "in determined path");
                }
            }

            classes.add(registerClass(
                    fullClassName, classSource));
        }
        if(setRoot == false) {

        }
        return classes;
    }

    static StringBuilder createClassSource(
            PianaRouterConfig routerConfig,
            String urlPattern,
            String className
    ) throws Exception {
        StringBuilder sb = initializeRouteClass(
                urlPattern, className);
        Set<String> httpMethodPatterns =
                routerConfig.getHttpMethodPatterns(urlPattern);
        if(httpMethodPatterns != null) {
            for(String httpMethodPattern : httpMethodPatterns) {
                PianaRouteConfig routeConfig =
                        routerConfig.getRouteConfig(
                                urlPattern, httpMethodPattern);
                appendRouteMethod(
                        urlPattern,
                        httpMethodPattern,
                        routeConfig,
                        sb);
            }
        }

        sb.append("}");
        return sb;
    }

    static void writeClassToFile(
            String outputClassPath,
            String className,
            StringBuilder classSource)
            throws Exception {
        if(outputClassPath == null ||
                outputClassPath.isEmpty())
            throw new Exception(
                    "output class path is null");
        File f = new File(outputClassPath.concat("/")
                .concat(className)
                .concat(".txt"));
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(classSource.toString().getBytes());
        fos.close();
    }

    static StringBuilder initializeRouteClass(
            String route,
            String className)
            throws Exception {
        final StringBuilder sb = new StringBuilder();
        sb.append("package ".concat(packageName).concat(";\n"));
        sb.append("import javax.ws.rs.container.Suspended;");
        sb.append("import javax.ws.rs.container.AsyncResponse;");
        sb.append("import javax.ws.rs.core.*;\n");
        sb.append("import javax.ws.rs.*;\n");
        sb.append("import javax.inject.Singleton;\n");
        sb.append("import java.lang.reflect.Method;\n");
        sb.append("import ir.piana.dev.server.route.*;\n");
        sb.append("import ir.piana.dev.server.role.*;\n");
        sb.append("import ir.piana.dev.server.response.*;\n");
        sb.append("import ir.piana.dev.server.session.*;\n");
        sb.append("import ir.piana.dev.server.asset.*;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");
        sb.append("@Singleton\n");
        sb.append("@Path(\"".concat(route).concat("\")\n"));
        sb.append("public class ".concat(className)
                .concat(" extends RouteService {\n"));
        return sb;
    }

//    static void appendRouteMethod(
//            String urlPattern,
//            String methodPattern,
//            PianaRouteConfig routeConfig,
//            StringBuilder sb)
//            throws Exception {
//        String methodKey = urlPattern
//                .concat(methodPattern);
//        //check if is asset must be have 0 or 1 path param
//        boolean isAsset = UtilityClass
//                .checkMethodCorrection(routeConfig);
//
//        String httpMethod = UtilityClass
//                .fetchHttpMethod(methodPattern);
////        String pathParamStrings = UtilityClass
////                .fetchPathParamStrings(methodPattern);
//        String[] pathParamList = UtilityClass
//                .fetchPathParamList(methodPattern);
//
//        //@GET or @POST or @DELETE or ...
//        sb.append("@".concat(httpMethod).concat("\n"));
//
//
//        //@Path({path-param}/{path-param})
//        if(pathParamList != null) {
//            sb.append("@Path(\""
//                    .concat(UtilityClass.createRestPathURL(
//                            pathParamList))
//                    .concat("\")\n"));
//        }
//
//        //@Consumes(MediaType.*)
//        String consumes = UtilityClass.createConsumes(
//                routeConfig, httpMethod);
//        if(consumes != null) {
//            sb.append("@Consumes(MediaType.APPLICATION_JSON)\n");
//        }
//
//        //add method signature
//        String methodSignature = null;
//        if(!routeConfig.isAsync())
//            methodSignature = UtilityClass
//                    .createMethodSignature(
//                            methodPattern, routeConfig);
//        else
//            methodSignature = UtilityClass
//                    .createAsyncMethodSignature(
//                            methodPattern, routeConfig);
//        if (methodSignature != null)
//            sb.append(methodSignature);
//
//        RoleType routeRoleType = RoleType.ADMIN;
//        try {
//            routeRoleType = RoleType
//                    .getFromName(routeConfig.getRole());
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//
//        sb.append("PianaResponse response = unauthorizedPianaResponse;\n");
//
//        if(isAsset
//                && pathParamList != null
//                && pathParamList.length == 1) {
////            sb.append("System.out.println(\"with param\");\n");
//            sb.append("if(!isAssetExist(\""
//                    .concat(routeConfig.getAssetPath())
//                    .concat("\",")
//                    .concat("uriInfo.getPathParameters().getFirst(\"")
//                    .concat(pathParamList[0].substring(
//                            0, pathParamList[0].indexOf(':')))
//                    .concat("\")")
//                    .concat(")) {\n")
//                    .concat("return createResponse(notFoundResponse(), null, httpHeaders);\n")
//                    .concat("}\n"));
//        } else {
////            sb.append("System.out.println(\"without param\");\n");
//        }
//        if (routeRoleType != RoleType.NEEDLESS) {
//            sb.append("final Session session = doAuthorization(httpHeaders);\n"
//                    .concat("if(!RoleType.")
//                    .concat(routeRoleType.getName())
//                    .concat(".isValid(session.getRoleType()))\n")
//                    .concat("return createResponse(response, session, httpHeaders);\n"));
//        } else
//            sb.append("final Session session = doRevivalSession(httpHeaders);\n");
//
//        sb.append("try {\n");
//        if(isAsset)
//            sb.append("PianaAssetResolver assetResolver = "
//                    .concat("registerAssetResolver(\"")
//                    .concat(methodKey)
//                    .concat("\",\"")
//                    .concat(routeConfig.getAssetPath())
//                    .concat("\");\n"));
//        String registerMethod = "Method m = registerMethod(\""
//                .concat(methodKey)
//                .concat("\",");
//
//        String callClassName = UtilityClass
//                .fetchCallClassName(routeConfig);
//        String callMethodName = UtilityClass
//                .fetchCallMethodName(routeConfig);
//        if(isAsset)
//            if(callClassName == null || callMethodName == null)
//                registerMethod = registerMethod.concat(
//                        "\"ir.piana.dev.server.route.AssetService\",")
//                        .concat("\"getAsset\",");
//            else
//                registerMethod = registerMethod.concat("\"")
//                        .concat(callClassName)
//                        .concat("\",\"")
//                        .concat(callMethodName)
//                        .concat("\",");
//        else
//            registerMethod = registerMethod.concat("\"")
//                    .concat(callClassName)
//                    .concat("\",\"")
//                    .concat(callMethodName)
//                    .concat("\",");
//        registerMethod = registerMethod.concat("Session.class,");
//
//        if(isAsset)
//            registerMethod = registerMethod
//                    .concat("PianaAssetResolver.class,");
//        registerMethod = registerMethod.concat(
//                "Map.class");
//        sb.append(registerMethod);
//        sb.append(");\n");
//
//        if(isAsset)
//            sb.append(
//                    "response = invokeMethod(m, session, assetResolver,");
//        else
//            sb.append("response = invokeMethod(m, session,");
//        if(routeConfig.isUrlInjected())
//            sb.append("createParameters(uriInfo,\""
//                    .concat(urlPattern)
//                    .concat("\",\"")
//                    .concat(methodPattern)
//                    .concat("\"),"));
//        else
//            sb.append("createParameters(uriInfo),");
//
//        String bodyJsonObjectName = UtilityClass
//                .fetchConsumeObjectName(
//                        routeConfig);
//        if(bodyJsonObjectName != null) {
//            sb.append(bodyJsonObjectName.concat(","));
//        }
//        sb.deleteCharAt(sb.length() - 1);
//        String excName = "exc_".concat(getRandomName(16));
//        sb.append(");\n} catch (Exception ".concat(excName).concat(") {\n"));
//        sb.append("System.out.println(".concat(excName).concat(".getMessage());\n"));
//        sb.append("logger.error(".concat(excName).concat(");\n"));
//        sb.append("response = internalServerErrorPianaResponse;\n}\n");
//        sb.append("return createResponse(response, session, httpHeaders);\n");
//        sb.append("}\n");
//    }

    static void appendRouteMethod(
            String urlPattern,
            String methodPattern,
            PianaRouteConfig routeConfig,
            StringBuilder sb)
            throws Exception {
        String methodKey = urlPattern
                .concat(methodPattern);
        //check if is asset must be have 0 or 1 path param
        boolean isAsset = UtilityClass
                .checkMethodCorrection(routeConfig);
        boolean isAsync = routeConfig.isAsync();

        String httpMethod = UtilityClass
                .fetchHttpMethod(methodPattern);
//        String pathParamStrings = UtilityClass
//                .fetchPathParamStrings(methodPattern);
        String[] pathParamList = UtilityClass
                .fetchPathParamList(methodPattern);

        //@GET or @POST or @DELETE or ...
        sb.append("@".concat(httpMethod).concat("\n"));

        //@Path({path-param}/{path-param})
        if(pathParamList != null) {
            sb.append("@Path(\""
                    .concat(UtilityClass.createRestPathURL(
                            pathParamList))
                    .concat("\")\n"));
        }

        //@Consumes(MediaType.*)
        String consumes = UtilityClass.createConsumes(
                routeConfig, httpMethod);
        if(consumes != null) {
            sb.append("@Consumes(MediaType.APPLICATION_JSON)\n");
        }

        //add method signature
        String methodSignature = null;
        if(!routeConfig.isAsync())
            methodSignature = UtilityClass
                    .createMethodSignature(
                            methodPattern, routeConfig);
        else
            methodSignature = UtilityClass
                    .createAsyncMethodSignature(
                            methodPattern, routeConfig);
        if (methodSignature != null)
            sb.append(methodSignature);

        if (isAsync) {
            sb.append("try {\n");

            ///asset resolver if isAsset true
            if(isAsset)
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

            String callClassName = UtilityClass
                    .fetchCallClassName(routeConfig);
            String callMethodName = UtilityClass
                    .fetchCallMethodName(routeConfig);
            if(isAsset)
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
            else
                registerMethod = registerMethod.concat("\"")
                        .concat(callClassName)
                        .concat("\",\"")
                        .concat(callMethodName)
                        .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");

            if(isAsset)
                registerMethod = registerMethod
                        .concat("PianaAssetResolver.class,");
            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");
            ///end of register method

            sb.append("executorService.execute(() -> {\n")
                    .append("try {\n");

            ///if isAsset true check if asset not exist
            if(isAsset) {
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
                        .concat("asyncResponse.resume(createResponse(notFoundResponse(), null, httpHeaders));\n")
                        .concat("return;\n")
                        .concat("}\n"));
                } else {
                    sb.append("if(!isAssetExist(\""
                            .concat(routeConfig.getAssetPath())
                            .concat("\",")
                            .concat("\"index.html\")")
                            .concat(") {\n")
                            .concat("asyncResponse.resume(createResponse(notFoundResponse(), null, httpHeaders));\n")
                            .concat("return;\n")
                            .concat("}\n"));
                }
            }

            ///if resource have role type
            RoleType routeRoleType = RoleType.ADMIN;
            try {
                routeRoleType = RoleType
                        .getFromName(routeConfig.getRole());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("final Session session = doAuthorization(httpHeaders);\n"
                        .concat("if(!RoleType.")
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("asyncResponse.resume(createResponse(unauthorizedPianaResponse, session, httpHeaders));\n"));
            } else
                sb.append("final Session session = doRevivalSession(httpHeaders);\n");
            ///end role type

            ///start of invoke method
            if(isAsset)
                sb.append(
                        "PianaResponse response = invokeMethod(m, session, assetResolver,");
            else
                sb.append("PianaResponse response = invokeMethod(m, session,");
            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = UtilityClass
                    .fetchConsumeObjectName(
                            routeConfig);
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat(","));
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");\n");
            sb.append("asyncResponse.resume(createResponse(response, session, httpHeaders));\n");
            ///end of invokeMethod
            String excName = "exc_".concat(getRandomName(16));
            sb.append("} catch (Exception ".concat(excName).concat(") {\n"));
            sb.append("logger.error(".concat(excName).concat(");\n"));
            sb.append("asyncResponse.resume(internalServerErrorPianaResponse);\n");
            sb.append("}\n});\n");
            ///end of executorService

            String excName2 = "exc_".concat(getRandomName(16));
            sb.append("} catch (Exception ".concat(excName2).concat(") {\n"));
            sb.append("logger.error(".concat(excName2).concat(");\n"));
            sb.append("asyncResponse.resume(internalServerErrorPianaResponse);\n");
            sb.append("}\n");
        } else {
            RoleType routeRoleType = RoleType.ADMIN;
            try {
                routeRoleType = RoleType
                        .getFromName(routeConfig.getRole());
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

            sb.append("PianaResponse response = unauthorizedPianaResponse;\n");

            ///if isAsset true check if asset not exist
            if(isAsset) {
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
                            .concat("return createResponse(notFoundResponse(), null, httpHeaders);\n")
                            .concat("}\n"));
                } else {
                    sb.append("if(!isAssetExist(\""
                            .concat(routeConfig.getAssetPath())
                            .concat("\",")
                            .concat("\"index.html\")")
                            .concat(") {\n")
                            .concat("return createResponse(notFoundResponse(), null, httpHeaders);\n")
                            .concat("}\n"));
                }
            }

            if (routeRoleType != RoleType.NEEDLESS) {
                sb.append("final Session session = doAuthorization(httpHeaders);\n"
                        .concat("if(!RoleType.")
                        .concat(routeRoleType.getName())
                        .concat(".isValid(session.getRoleType()))\n")
                        .concat("return createResponse(response, session, httpHeaders);\n"));
            } else
                sb.append("final Session session = doRevivalSession(httpHeaders);\n");

            sb.append("try {\n");

            ///assetResolver if isAsset is true
            if(isAsset)
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

            String callClassName = UtilityClass
                    .fetchCallClassName(routeConfig);
            String callMethodName = UtilityClass
                    .fetchCallMethodName(routeConfig);
            if(isAsset)
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
            else
                registerMethod = registerMethod.concat("\"")
                        .concat(callClassName)
                        .concat("\",\"")
                        .concat(callMethodName)
                        .concat("\",");
            registerMethod = registerMethod.concat("Session.class,");

            if(isAsset)
                registerMethod = registerMethod
                        .concat("PianaAssetResolver.class,");
            registerMethod = registerMethod.concat(
                    "Map.class");
            sb.append(registerMethod);
            sb.append(");\n");

            ///end of register method

            ///start of invoke method
            if(isAsset)
                sb.append(
                        "response = invokeMethod(m, session, assetResolver,");
            else
                sb.append("response = invokeMethod(m, session,");
            if(routeConfig.isUrlInjected())
                sb.append("createParameters(uriInfo,\""
                        .concat(urlPattern)
                        .concat("\",\"")
                        .concat(methodPattern)
                        .concat("\"),"));
            else
                sb.append("createParameters(uriInfo),");
            String bodyJsonObjectName = UtilityClass
                    .fetchConsumeObjectName(
                            routeConfig);
            if(bodyJsonObjectName != null) {
                sb.append(bodyJsonObjectName.concat(","));
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append(");\n");
            ///end of invokeMethod
            String excName = "exc_".concat(getRandomName(16));
            sb.append("} catch (Exception ".concat(excName).concat(") {\n"));
            sb.append("System.out.println(".concat(excName).concat(".getMessage());\n"));
            sb.append("logger.error(".concat(excName).concat(");\n"));
            sb.append("response = internalServerErrorPianaResponse;\n}\n");
            sb.append("return createResponse(response, session, httpHeaders);\n");
        }
        sb.append("}\n");
    }

    private static String getRandomName(int len)
            throws Exception {
        return HexConverter.toHexString(
                SecureRandomMaker.makeByteArray(
                        len, SecureRandomType.SHA_1_PRNG));
    }

    private static String getClassName(String urlPattern)
            throws Exception {
        if(urlPattern == null ||
                urlPattern.isEmpty() ||
                urlPattern.equalsIgnoreCase("/"))
            return "RootClass_".concat(getRandomName(8));
        char[] chars = urlPattern.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '-' || chars[i] == '/') {
                chars[++i] = String.valueOf(chars[i])
                        .toUpperCase().charAt(0);
            }
        }
        return new String(chars)
                .replaceFirst("/", "")
                .replaceAll("/", "_")
                .replaceAll("-", "");
    }

    private static Class registerClass(
            String fullClassName,
            StringBuilder sb)
            throws NoSuchFieldException,
            IllegalAccessException {
        // A byte array output stream containing the bytes that would be written to the .class file
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return sb;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaFileManager javaFileManager = new ForwardingJavaFileManager(
                ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null)) {

            @Override
            public JavaFileObject getJavaFileForOutput(Location location,
                                                       String className,
                                                       JavaFileObject.Kind kind,
                                                       FileObject sibling) throws IOException {
                return simpleJavaFileObject;
            }
        };

        ToolProvider.getSystemJavaCompiler().getTask(
                null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();

        final byte[] bytes = byteArrayOutputStream.toByteArray();

        // use the unsafe class to load in the class bytes
        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);
        final Class aClass = unsafe.defineClass(
                fullClassName, bytes, 0, bytes.length,
                RouteClassGeneratorEx.class.getClassLoader(),
                null);
        return aClass;
    }

    private static class UtilityClass {
        private static String getMethodName(String methodPattern)
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
                return methodName.concat(getRandomName(8));

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

        private static String fetchHttpMethod(
                String methodPatterns) {
            if(!methodPatterns.contains("#"))
                return methodPatterns;
            return methodPatterns.substring(0,
                    methodPatterns.indexOf("#"));
        }

        private static String fetchPathParamStrings(
                String methodPattern) {
            if(methodPattern.contains("#"))
                return methodPattern.substring(
                        methodPattern.indexOf("#") + 1,
                        methodPattern.length());
            return null;
        }

        private static String[] fetchPathParamList(
                String methodPattern) {
            String pathParamStrings =
                    fetchPathParamStrings(methodPattern);
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

        private static String createRestPathURL(
                String[] pathParamList) {
            String path = "";
            for(String pathParam :
                    pathParamList)
                path = path.concat("/{")
                        .concat(pathParam).concat("}");
            return path;
        }

        private static String createConsumes(
                PianaRouteConfig routeConfig,
                String httpMethod)
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

        private static String fetchConsumeObjectName(
                PianaRouteConfig routeConfig) {
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

        private static String createBodyObjectParam(
                PianaRouteConfig routeConfig) {
            String bodyJsonObject = routeConfig
                    .getBodyJsonObject();
            if(bodyJsonObject != null &&
                    !bodyJsonObject.isEmpty()) {
                return bodyJsonObject
                        .concat(" ")
                        .concat(UtilityClass
                                .fetchConsumeObjectName(
                                        routeConfig));
            }
            return null;
        }

        public static boolean checkMethodCorrection(
                PianaRouteConfig routeConfig)
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

        public static String createMethodSignature(
                String methodPattern,
                PianaRouteConfig routeConfig)
                throws Exception {
            String methodName = getMethodName(methodPattern);
            String signature = "public Response "
                    .concat(methodName)
                    .concat("(@Context HttpHeaders httpHeaders,")
                    .concat("@Context UriInfo uriInfo");

            //add body object to parameter list
            String bodyObjectParam = UtilityClass
                    .createBodyObjectParam(routeConfig);
            if(bodyObjectParam != null) {
                signature = signature.concat(",")
                        .concat(bodyObjectParam);
            }

            return signature.concat(") throws Exception {\n");
        }

        public static String createAsyncMethodSignature(
                String methodPattern,
                PianaRouteConfig routeConfig)
                throws Exception {
            String methodName = getMethodName(methodPattern);
            String signature = "public void "
                    .concat(methodName)
                    .concat("(@Suspended final AsyncResponse asyncResponse,")
                    .concat("@Context HttpHeaders httpHeaders,")
                    .concat("@Context UriInfo uriInfo");

            //add body object to parameter list
            String bodyObjectParam = UtilityClass
                    .createBodyObjectParam(routeConfig);
            if(bodyObjectParam != null) {
                signature = signature.concat(",")
                        .concat(bodyObjectParam);
            }

            return signature.concat(") throws Exception {\n");
        }

        private static String fetchCallClassName(
                PianaRouteConfig routeConfig) throws Exception {
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

        private static String fetchCallMethodName(
                PianaRouteConfig routeConfig) throws Exception {
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
    }
}
