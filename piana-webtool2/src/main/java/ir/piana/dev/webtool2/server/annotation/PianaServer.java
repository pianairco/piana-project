package ir.piana.dev.webtool2.server.annotation;

import ir.piana.dev.webtool2.server.http.HttpServerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ASUS on 7/28/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface PianaServer {
    HttpServerType serverType() default HttpServerType.JETTY;
    String httpIp() default "localhost";
    int httpPort() default 8000;
    String httpDocIp() default "localhost";
    int httpDocPort() default 8000;
    String docStartUrl() default "piana-doc";
    String httpBaseRoute() default "";
    boolean removeOtherCookies() default false;
    String outputClassPath() default "./classes";
    PianaServerSession serverSession() default @PianaServerSession;
    PianaServerCORS serverCORS() default @PianaServerCORS;
}
