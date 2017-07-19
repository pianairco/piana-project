package ir.piana.dev.server.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.server.config.PianaConfigReader;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import ir.piana.dev.server.config.PianaServerConfig;
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
    private static String defaultDocumentStartUrl = "piana-doc";

    public static Set<Class<?>> generateRouteClasses(
            PianaRouterConfig routerConfig,
            PianaServerConfig serverConfig)
            throws Exception {
        String documentStartUrl = serverConfig.getDocumentStartUrl() == null ?
                defaultDocumentStartUrl : serverConfig.getDocumentStartUrl();
        Set<String> urlPatterns = routerConfig.getUrlPatterns();
        Set<Class<?>> classes = new HashSet<>();

        boolean setRoot = false;
        for(String urlPattern : urlPatterns) {
            if(urlPattern.length() > 1) {
                String tempUrlPattern = urlPattern.startsWith("/") ?
                        urlPattern.substring(1, urlPattern.substring(1).contains("/") ?
                                (urlPattern.indexOf('/', 1) < 1 ? 1 : urlPattern.indexOf('/', 1))
                                : urlPattern.length())
                        : urlPattern.substring(0, urlPattern.contains("/") ?
                        (urlPattern.indexOf('/') < 0 ? urlPattern.length() : urlPattern.indexOf('/'))
                        : urlPattern.length());
                if(tempUrlPattern.equalsIgnoreCase(documentStartUrl))
                    throw new Exception("any url not can start with " + documentStartUrl);
            }
            final String className = getClassName(urlPattern);
            final String fullClassName = packageName
                    .replace('.', '/')
                    .concat("/")
                    .concat(className);
            StringBuilder classSource = createClassSource(
                    routerConfig, urlPattern, className);

            writeClassToFile(serverConfig.getOutputClassPath(),
                    className, classSource);

            classes.add(registerClass(
                    fullClassName, classSource));
        }
        if(setRoot == false) {

        }
        return classes;
    }

    public static Set<Class<?>> generateDocumentClasses(
            PianaRouterConfig routerConfig,
            PianaServerConfig serverConfig)
            throws Exception {
        DocumentService.routerConfig = routerConfig;
        DocumentService.serverConfig = serverConfig;
        String documentStartUrl = serverConfig.getDocumentStartUrl() == null ?
                defaultDocumentStartUrl : serverConfig.getDocumentStartUrl();
        DocumentService.documentStartUrl = documentStartUrl;

        Set<Class<?>> documentClasses = new HashSet<>();

        //add document model class
        final String documentClassName = getClassName(documentStartUrl);
        final String fullDocumentClassName = packageName
                .replace('.', '/')
                .concat("/")
                .concat(documentClassName);
        StringBuilder documentClassSource = createDocumentClassSource(
                documentStartUrl, documentClassName);

        writeClassToFile(serverConfig.getOutputClassPath(),
                        documentClassName, documentClassSource);

        documentClasses.add(registerClass(
                fullDocumentClassName, documentClassSource));

        //add json model class
        final String jsonDocumentClassName = getClassName(documentStartUrl
                .concat("JsonModel"));
        final String fullJsonDocumentClassName = packageName
                .replace('.', '/')
                .concat("/")
                .concat(jsonDocumentClassName);
        StringBuilder jsonDocumentClassSource = createDocumentJsonClassSource(
                documentStartUrl.concat("/json-model"), jsonDocumentClassName);

        writeClassToFile(serverConfig.getOutputClassPath(),
                jsonDocumentClassName, jsonDocumentClassSource);

        documentClasses.add(registerClass(
                fullJsonDocumentClassName, jsonDocumentClassSource));

        return documentClasses;
    }

    static StringBuilder createClassSource(
            PianaRouterConfig routerConfig,
            String urlPattern,
            String className
    ) throws Exception {
        StringBuilder sb = initializeRouteClass(
                urlPattern, className);
        //open class brace
        sb.append("{\n");
        Set<String> httpMethodPatterns =
                routerConfig.getHttpMethodPatterns(urlPattern);
        if(httpMethodPatterns != null) {
            for(String httpMethodPattern : httpMethodPatterns) {
                PianaRouteConfig routeConfig =
                        routerConfig.getRouteConfig(
                                urlPattern, httpMethodPattern);
                sb.append(MethodCreator.getInstance(
                        urlPattern, httpMethodPattern, routeConfig)
                        .createMethod());
            }
        }

        sb.append("}");
        return sb;
    }

    static StringBuilder createDocumentClassSource(
            String urlPattern,
            String className
    ) throws Exception {
        StringBuilder sb = initializeRouteClass(
                urlPattern, className);
        //open class brace
        sb.append("{\n");

        String httpMethodPattern = "GET";

        StringBuilder routeBuilder = new StringBuilder();

        routeBuilder.append("{\n")
                .append("\"handler\": \"ir.piana.dev.server.route.DocumentService.getPianaDocument\",\n")
                .append("\"path-params\": [],\n")
                .append("\"query-params\": [],\n")
                .append("\"role\": \"GUEST\"\n")
                .append("}");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(routeBuilder.toString());
        PianaRouteConfig routeConfig = PianaConfigReader.createPianaRouteConfig(jsonNode);
        sb.append(MethodCreator.getInstance(
                urlPattern, httpMethodPattern, routeConfig)
                .createMethod());

        //close class brace
        sb.append("}");
        return sb;
    }

    static StringBuilder createDocumentJsonClassSource(
            String urlPattern,
            String className
    ) throws Exception {
        StringBuilder sb = initializeRouteClass(
                urlPattern, className);
        //open class brace
        sb.append("{\n");

        String httpMethodPattern = "GET";

        StringBuilder routeBuilder = new StringBuilder();

        routeBuilder.append("{\n")
                .append("\"handler\": \"ir.piana.dev.server.route.DocumentService.getPianaJson\",\n")
                .append("\"path-params\": [],\n")
                .append("\"query-params\": [],\n")
                .append("\"role\": \"GUEST\"\n")
                .append("}");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(routeBuilder.toString());
        PianaRouteConfig routeConfig = PianaConfigReader.createPianaRouteConfig(jsonNode);
        sb.append(MethodCreator.getInstance(
                urlPattern, httpMethodPattern, routeConfig)
                .createMethod());

        //close class brace
        sb.append("}");
        return sb;
    }

    static void writeClassToFile(
            String outputClassPath,
            String className,
            StringBuilder classSource)
            throws Exception {

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
            } catch (Exception e) {
                logger.error("not can make class file " +
                        "in determined path");
            }
        }
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
                .concat(" extends RouteService "));
        return sb;
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
                RouteClassGenerator.class.getClassLoader(),
                null);
        return aClass;
    }
}