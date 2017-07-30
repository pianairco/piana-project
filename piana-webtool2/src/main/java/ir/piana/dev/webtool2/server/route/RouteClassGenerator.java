package ir.piana.dev.webtool2.server.route;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.webtool2.server.annotation.*;
import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public static Set<Class<?>> generateHandlerClasses(
            PianaServer pianaServer)
            throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        List<Class> handlerClasses = AnnotationController
                .getHandlerClasses();
        for (Class targetClass : handlerClasses) {
            HandlerClassCreator handlerClassCreator =
                    HandlerClassCreator.getInstance(targetClass);
            String classSource = handlerClassCreator.create();
            writeClassToFile(pianaServer.outputClassPath(),
                    handlerClassCreator.getClassName(), classSource);
            classes.add(registerClass(
                    handlerClassCreator.getFullClassName(),
                    classSource));
        }
        return classes;
    }

    static void writeClassToFile(
            String outputClassPath,
            String className,
            String classSource)
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
                        .concat(".java"));
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(classSource.getBytes());
                fos.close();
            } catch (Exception e) {
                logger.error("not can make class file " +
                        "in determined path");
            }
        }
    }

    private static Class registerClass(
            String fullClassName,
            String classSource)
            throws NoSuchFieldException,
            IllegalAccessException {
        // A byte array output stream containing the bytes that would be written to the .class file
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject
                = new SimpleJavaFileObject(URI.create(fullClassName + ".java"), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return classSource;
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