package ir.piana.dev.server.route;

import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.role.RoleType;
import ir.piana.dev.server.session.Session;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.*;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;

/**
 * @author Mohammad Rahmati, 5/7/2017 5:20 PM
 */
public class RouteClassGenerator {
    final static Logger logger =
            Logger.getLogger(RouteClassGenerator.class);
    private static final String packageName =
            "ir.piana.dev.server.route";

    public static Set<Class<?>> generateRouteClasses(
            PianaRouterConfig routerConfig,
            String outputClassPath)
            throws Exception {
        Set<String> routes = routerConfig.getUrlPattens();
        Set<Class<?>> classes = new HashSet<>();
        /**
         * setRoot = "/" default for index.html
         */
        boolean setRoot = false;
        for(String route : routes) {
            final String className = getClassName(route);
            final String fullClassName = packageName
                    .replace('.', '/')
                    .concat("/")
                    .concat(className);
            StringBuilder classSource = createClassSource(
                    routerConfig, route, className);

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
            String route, String className
    ) throws Exception {
        StringBuilder sb = initializeRouteClass(
                route, className);
        Set<String> httpMethods =
                routerConfig.getHttpMethodPatterns(route);
        if(httpMethods != null) {
            for(String httpMethod : httpMethods) {
                String fixHttpMethod = httpMethod;
                if(httpMethod.contains("#"))
                    fixHttpMethod = httpMethod.substring(
                            0, httpMethod.indexOf("#"));
                PianaRouterConfig.PianaRouteConfig routeConfig =
                        routerConfig.getRouteConfig(
                                route, httpMethod);
                appendRouteMethod(
                        route.concat(httpMethod),
                        fixHttpMethod,
                        httpMethod,
                        routeConfig, sb);
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
        sb.append("import javax.ws.rs.core.*;\n");
        sb.append("import javax.ws.rs.*;\n");
        sb.append("import javax.inject.Singleton;\n");
        sb.append("import java.lang.reflect.Method;\n");
        sb.append("import ir.piana.dev.server.route.*;\n");
        sb.append("import ir.piana.dev.server.role.*;\n");
        sb.append("import ir.piana.dev.server.response.*;\n");
        sb.append("import ir.piana.dev.server.session.*;\n");
        sb.append("import ir.piana.dev.server.asset.*;\n");
        sb.append("@Singleton\n");
        sb.append("@Path(\"".concat(route).concat("\")\n"));
        sb.append("public class ".concat(className)
                .concat(" extends RouteService {\n"));
        return sb;
    }

    static void appendRouteMethod(
            String urlPath,
            String httpMethod,
            String methodPattern,
            PianaRouterConfig.PianaRouteConfig routeConfig,
            StringBuilder sb)
            throws Exception {
        sb.append("@".concat(httpMethod).concat("\n"));
        String path = new String("");
        if(routeConfig.getPathParams() != null) {
            for(String pathParam :
                    routeConfig.getPathParams()) {
                path = path.concat("/{")
                        .concat(pathParam).concat("}");
            }
            sb.append("@Path(\""
                    .concat(path)
                    .concat("\")\n"));
        }
        String bodyJsonObject =
                routeConfig.getBodyJsonObject();
        String bodyJsonObjectName = null;
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            if(httpMethod.equalsIgnoreCase("GET") ||
                    httpMethod.equalsIgnoreCase("HEAD") ||
                    httpMethod.equalsIgnoreCase("DELETE") ||
                    httpMethod.equalsIgnoreCase("TRACE") ||
                    httpMethod.equalsIgnoreCase("OPTIONS") ||
                    httpMethod.equalsIgnoreCase("CONNECT"))
                throw new Exception("http method don't support body");
            sb.append("@Consumes(MediaType.APPLICATION_JSON)\n");
            bodyJsonObjectName = bodyJsonObject.substring(
                    bodyJsonObject.lastIndexOf(".") + 1);
        }

        String methodName = getMethodName(methodPattern);
        RoleType methodRoleType = RoleType.ADMIN;
        try {
            methodRoleType = RoleType
                    .getFromName(routeConfig.getRole());
//            sb.append("@Role(roleType = RoleType."
//                    .concat(methodRoleType.getName()).concat(")\n"));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        sb.append("public Response ".concat(methodName)
                .concat("(@Context HttpHeaders httpHeaders,"));
        boolean isAsset = routeConfig.isAsset();

        List<String> methodParams = new ArrayList<>();
        if(routeConfig.getPathParams() != null) {
            if(isAsset &&
                    routeConfig.getPathParams().size() != 0 &&
                    routeConfig.getPathParams().size() != 1) {
                throw new Exception("this asset path not set corrected!");
            }
            for(String pathParam :
                    routeConfig.getPathParams()) {
                String pathParamName = pathParam;
                if(pathParam.contains(":"))
                    pathParamName = pathParam.substring(0,
                            pathParam.indexOf(":"));
                methodParams.add(pathParamName);
                sb.append("@PathParam(\""
                        .concat(pathParamName)
                        .concat("\") String ")
                        .concat(pathParamName.concat(",")));
            }
        }
        if(routeConfig.getQueryParams() != null) {
            for(String queryParam :
                    routeConfig.getQueryParams()) {
                methodParams.add(queryParam);
                sb.append("@QueryParam(\"".concat(queryParam)
                        .concat("\") String ")
                        .concat(queryParam).concat(","));
            }
        }

        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            sb.append(bodyJsonObject
                    .concat(" ")
                    .concat(bodyJsonObjectName)
                    .concat(","));
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(") throws Exception {\n");
        String handler = routeConfig.getHandler();
        String callMethodName = null;
        String callClassName = null;
        if(handler != null && !handler.isEmpty()) {
            callMethodName = handler.substring(
                    handler.lastIndexOf(".") + 1);
            callClassName = handler.substring(
                    0, handler.lastIndexOf("."));
        }
        if((callClassName == null || callMethodName == null)
                && !routeConfig.isAsset()) {
            throw new Exception("handler is incorrect.");
        }

        String paramListStr = "";
        Class<?>[] paramList = null;
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty())
            paramList = new Class<?>[methodParams.size() + 2];
        else
            paramList = new Class<?>[methodParams.size() + 1];
        int i = 0;
        paramList[i++] = Session.class;
        for(String p : methodParams) {
            paramListStr = paramListStr.concat("String.class,");
            paramList[i++] = String.class;
        }
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            paramListStr = paramListStr.concat(bodyJsonObject).concat(".class,");
            paramList[i++] = Class.forName(bodyJsonObject);
        }
        sb.append("PianaResponse response = unauthorizedPianaResponse;\n");
        sb.append("Session session = null;\n");

        if(isAsset && !methodParams.isEmpty()) {
            sb.append("if(!isAssetExist(\""
                    .concat(routeConfig.getAssetPath())
                    .concat("\",")
                    .concat(methodParams.get(0))
                    .concat(")) {\n")
                    .concat("return createResponse(notFoundResponse(), null, httpHeaders);\n")
                    .concat("}\n"));
        }
        if (methodRoleType != RoleType.NEEDLESS) {
            sb.append("session = doAuthorization(httpHeaders);\n"
                    .concat("if(!RoleType.")
                    .concat(methodRoleType.getName())
                    .concat(".isValid(session.getRoleType()))\n")
                    .concat("return createResponse(response, session, httpHeaders);\n"));
        } else
            sb.append("session = doRevivalSession(httpHeaders);\n");

        sb.append("try {\n");
        if(isAsset)
            sb.append("PianaAssetResolver assetResolver = "
                            .concat("registerAssetResolver(\"")
                    .concat(urlPath)
                    .concat("\",\"")
                    .concat(routeConfig.getAssetPath())
                    .concat("\");\n"));
        String registerMethod = "Method m = registerMethod(\""
                .concat(urlPath)
                .concat("\",\"");
        if(isAsset)
            if(callClassName == null || callMethodName == null)
                registerMethod = registerMethod.concat(
                        "ir.piana.dev.server.route.AssetService")
                        .concat("\",\"getAsset\",");
            else
                registerMethod = registerMethod.concat(callClassName)
                        .concat("\",\"")
                        .concat(callMethodName).concat("\",");
        else
            registerMethod = registerMethod.concat(callClassName)
                    .concat("\",\"")
                    .concat(callMethodName).concat("\",");
        registerMethod = registerMethod.concat("Session.class,");

        if(isAsset)
            registerMethod = registerMethod
                    .concat("PianaAssetResolver.class,");
        registerMethod = registerMethod.concat(paramListStr);
        sb.append(registerMethod);
        sb.deleteCharAt(sb.length() - 1);
        sb.append(");\n");

        if(isAsset)
            sb.append("response = invokeMethod(m, session, assetResolver,");
        else
            sb.append("response = invokeMethod(m, session,");
        for (String methodParam : methodParams) {
            sb.append(methodParam.concat(","));
        }
        if(bodyJsonObject != null &&
                !bodyJsonObject.isEmpty()) {
            sb.append(bodyJsonObjectName.concat(","));
        }
        sb.deleteCharAt(sb.length() - 1);
        String excName = "exc_".concat(getRandomName(16));
        sb.append(");\n} catch (Exception ".concat(excName).concat(") {\n"));
        sb.append("System.out.println(".concat(excName).concat(".getMessage());\n"));
        sb.append("logger.error(".concat(excName).concat(");\n"));
        sb.append("response = internalServerErrorPianaResponse;\n}\n");
        sb.append("return createResponse(response, session, httpHeaders);\n");
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

    private static String getMethodName(String methodPattern)
            throws Exception {
        if(methodPattern == null)
            throw new Exception("method pattern is null.");
        else if(methodPattern.startsWith("GET"))
            methodPattern = methodPattern.replace("GET", "get");
        else if(methodPattern.startsWith("POST"))
            methodPattern = methodPattern.replace("POST", "post");
        else if(methodPattern.startsWith("PUT"))
            methodPattern = methodPattern.replace("PUT", "put");
        else if(methodPattern.startsWith("DELETE"))
            methodPattern = methodPattern.replace("DELETE", "delete");
        else if(methodPattern.startsWith("OPTIONS"))
            methodPattern = methodPattern.replace("OPTIONS", "options");
        else if(methodPattern.startsWith("HEAD"))
            methodPattern = methodPattern.replace("HEAD", "head");
        else
            throw new Exception("method pattern is incorrect.");

        if(!methodPattern.contains("#"))
            return methodPattern.concat(getRandomName(8));

        char[] chars = methodPattern.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(chars[i] == '#' || chars[i] == ':') {
                chars[++i] = String.valueOf(chars[i])
                        .toUpperCase().charAt(0);
            }
        }
        return new String(chars)
                .replaceAll("#", "")
                .replaceAll(":", "");
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
                RouteClassGenerator.class.getClassLoader(),
                null);
        return aClass;
    }
}
