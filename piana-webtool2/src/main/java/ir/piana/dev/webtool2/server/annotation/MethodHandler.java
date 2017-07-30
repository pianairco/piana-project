package ir.piana.dev.webtool2.server.annotation;

import ir.piana.dev.server.role.RoleType;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ASUS on 7/28/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on class level
public @interface MethodHandler {
    RoleType roleType() default RoleType.NEEDLESS;
    String httpMethod() default "GET";
    String requiredRole() default "NEEDLESS";
    boolean isSync() default true;
    boolean urlInjected() default false;
}
