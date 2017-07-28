package ir.piana.dev.webtool2.server.role;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Mohammad Rahmati, 4/12/2017 2:30 PM
 */
@NameBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Role {
    RoleType roleType() default RoleType.NEEDLESS;
}
