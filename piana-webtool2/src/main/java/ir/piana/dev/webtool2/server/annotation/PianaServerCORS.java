package ir.piana.dev.webtool2.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ASUS on 7/28/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface PianaServerCORS {
    String[] allowOrigin() default {""};
    String[] allowHeaders() default {"origin", "content-type", "accept", "authorization"};
    boolean allowCredentials() default true;
    String[] allowMethods() default {"GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"};
}
