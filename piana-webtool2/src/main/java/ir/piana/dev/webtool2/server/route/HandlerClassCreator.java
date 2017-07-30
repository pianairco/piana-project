package ir.piana.dev.webtool2.server.route;

import ir.piana.dev.secure.random.SecureRandomMaker;
import ir.piana.dev.secure.random.SecureRandomType;
import ir.piana.dev.secure.util.HexConverter;
import ir.piana.dev.webtool2.server.annotation.*;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by SYSTEM on 7/29/2017.
 */
public abstract class HandlerClassCreator {
    final static Logger logger =
            Logger.getLogger(HandlerClassCreator.class);
    private static final String packageName =
            "ir.piana.dev.webtool2.server.route";
    protected Handler handler;
    protected Class targetClass;
    private String className;
    private String fullClassName;
    HandlerMethodCreator handlerMethodCreator;

    public String getClassName() {
        return this.className;
    }

    public String getFullClassName() {
        return this.fullClassName;
    }

    private HandlerClassCreator(
            Class targetClass,
            String className,
            String fullClassName,
            Handler handler) {
        this.targetClass = targetClass;
        this.className = className;
        this.fullClassName = fullClassName;
        this.handler = handler;
    }

    public String create() throws Exception {
        StringBuilder sb = new StringBuilder("package "
                .concat(packageName)
                .concat(";\n"));
        sb.append(makeImports());
        sb.append(makeClassAnnotation());
        sb.append(makeClassSignature());
        sb.append(makeClassMethods());
        sb.append(makeClassEnd());
        return sb.toString();
    }

    private final String makeImports() {
        StringBuilder sb = new StringBuilder();
//        sb.append("import javax.ws.rs.container.Suspended;");
//        sb.append("import javax.ws.rs.container.AsyncResponse;");
        sb.append("import javax.ws.rs.core.*;\n");
        sb.append("import javax.ws.rs.*;\n");
        sb.append("import javax.inject.Singleton;\n");
        sb.append("import java.lang.reflect.Method;\n");
        sb.append("import ir.piana.dev.webtool2.server.route.*;\n");
        sb.append("import ir.piana.dev.webtool2.server.role.*;\n");
        sb.append("import ir.piana.dev.webtool2.server.response.*;\n");
        sb.append("import ir.piana.dev.webtool2.server.session.*;\n");
        sb.append("import java.util.List;\n");
        sb.append("import java.util.Map;\n");

        if(handler.handlerType() == HandlerType.ASSET_HANDLER) {
            sb.append("import ir.piana.dev.webtool2.server.asset.*;\n");
            AssetHandler assetHandler = AnnotationController
                    .getAssetHandler(targetClass);
            if(!assetHandler.isSync()) {
                sb.append("import javax.ws.rs.container.Suspended;\n");
                sb.append("import javax.ws.rs.container.AsyncResponse;\n");
            }
        } else {
            List<Method> handlerMethods = AnnotationController.getHandlerMethods(targetClass);
            for(Method method : handlerMethods) {
                MethodHandler methodHandler = AnnotationController.getMethodHandler(method);
                if(!methodHandler.isSync()) {
                    sb.append("import javax.ws.rs.container.Suspended;\n");
                    sb.append("import javax.ws.rs.container.AsyncResponse;\n");
                    break;
                }
            }
        }
        return sb.toString();
    }

    private final String makeClassAnnotation() {
        StringBuilder sb = new StringBuilder();
        sb.append("@Singleton\n");
        sb.append("@Path(\"".concat(handler.baseUrl()).concat("\")\n"));
        return sb.toString();
    }

    private final String makeClassSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append("public class ".concat(className)
                .concat(" extends RouteService {\n"));
        return sb.toString();
    }

    protected abstract String makeClassMethods() throws Exception;

    private final String makeClassEnd () {
        return "}";
    }

    // --------- factory method ---------------------

    public static HandlerClassCreator getInstance(
            Class targetClass)
            throws Exception {
        Handler handler = AnnotationController.getHandler(targetClass);
        final String className = getClassName(handler.baseUrl());
        final String fullClassName = packageName
                .replace('.', '/')
                .concat("/")
                .concat(className);
        if(handler.handlerType() == HandlerType.ASSET_HANDLER) {
            return new AssetHandlerClassCreator(
                    targetClass, className, fullClassName, handler);
        } else {
            return new MethodHandlerClassCreator(
                    targetClass, className, fullClassName, handler);

        }
    }

    // --------- utility methods --------------------

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
        if(urlPattern.startsWith("/"))
            urlPattern = urlPattern.substring(1);
        char[] chars = urlPattern.toCharArray();
        chars[0] = String.valueOf(chars[0])
                .toUpperCase().charAt(0);
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

    // ------------ sub classes -----------------

    private static class AssetHandlerClassCreator
            extends HandlerClassCreator {

        private AssetHandler assetHandler;

        private AssetHandlerClassCreator(
                Class targetClass,
                String className,
                String fullClassName,
                Handler handler)
                throws Exception {
            super(targetClass, className, fullClassName, handler);
            handlerMethodCreator = HandlerMethodCreator.getAssetHandlerInstance(targetClass);
        }

        @Override
        protected String makeClassMethods() throws Exception {
            StringBuilder sb = new StringBuilder();
            AssetHandler assetHandler = AnnotationController
                        .getAssetHandler(targetClass);
            sb.append("private PianaAssetResolver assetResolver = PianaAssetResolver\n"
                            .concat(".getInstance(\"" + assetHandler.assetPath() + "\");\n"));

            sb.append(handlerMethodCreator.create());
            return sb.toString();
        }
    }

    private static class MethodHandlerClassCreator
            extends HandlerClassCreator {
        private MethodHandler methodHandler;

        private MethodHandlerClassCreator(
                Class targetClass,
                String className,
                String fullClassName,
                Handler handler) {
            super(targetClass, className, fullClassName, handler);
        }

        @Override
        protected String makeClassMethods() throws Exception {
            StringBuilder sb = new StringBuilder("");
            List<Method> handlerMethods = AnnotationController
                        .getHandlerMethods(targetClass);
            for (Method method : handlerMethods) {
                HandlerMethodCreator handlerMethodCreator = HandlerMethodCreator
                        .getMethodHandlerInstance(method);
                sb.append(handlerMethodCreator.create());
            }
            return sb.toString();
        }
    }
}
