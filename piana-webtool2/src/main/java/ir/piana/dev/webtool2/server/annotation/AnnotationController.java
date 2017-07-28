package ir.piana.dev.webtool2.server.annotation;

import org.reflections.Reflections;

import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by ASUS on 7/28/2017.
 */
public abstract class AnnotationController {
    public static Class getServerClass()
            throws Exception {
        Reflections reflections = new Reflections();
        Set<Class<?>> typesAnnotatedWith = reflections
                .getTypesAnnotatedWith(PianaServer.class);
        if(typesAnnotatedWith == null || typesAnnotatedWith.isEmpty())
            throw new Exception(
                    "any class not annotated with @PianaServer");
        else if(typesAnnotatedWith.size() == 1)
            return (Class) typesAnnotatedWith.toArray()[0];
        else
            throw new Exception(
                    "more than one class annotated with @PianaServer");
    }

    public static PianaServer getPianaServer(Class targetClass) {
        Annotation annotation = targetClass
                .getAnnotation(PianaServer.class);
        return annotation == null ? null : (PianaServer) annotation;
    }

    public static List<Class> getAssetHandlerClasses() {
        List<Class> classes = new ArrayList<>();
        Reflections reflections = new Reflections();
        Set<Class<?>> typesAnnotatedWith = reflections
                .getTypesAnnotatedWith(AssetHandler.class);
        if(typesAnnotatedWith != null && !typesAnnotatedWith.isEmpty())
            classes.addAll(typesAnnotatedWith);
        return classes;
    }

    public static AssetHandler getAssetHandler(Class targetClass) {
        Annotation annotation = targetClass
                .getAnnotation(AssetHandler.class);
        return annotation == null ? null : (AssetHandler) annotation;
    }

    public static List<Class> getHandlerClasses() {
        List<Class> classes = new ArrayList<>();
        Reflections reflections = new Reflections();
        Set<Class<?>> typesAnnotatedWith = reflections
                .getTypesAnnotatedWith(Handler.class);
        if(typesAnnotatedWith != null && !typesAnnotatedWith.isEmpty())
            classes.addAll(typesAnnotatedWith);
        return classes;
    }

    public static Handler getHandler(Class targetClass) {
        Annotation annotation = targetClass
                .getAnnotation(Handler.class);
        return annotation == null ? null : (Handler) annotation;
    }

    public static void main(String[] args) {
        Reflections reflections = new Reflections();
        Set<Class<?>> typesAnnotatedWith = reflections
                .getTypesAnnotatedWith(Path.class);
        typesAnnotatedWith.forEach(
                a -> System.out.println(a.toString()));
    }
}
