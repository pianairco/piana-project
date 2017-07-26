package ir.piana.dev.sample.rest;

import ir.piana.dev.sample.model.UserModel;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.role.RoleType;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 6/3/2017 1:40 PM
 */
public class AuthRest {
    public static PianaResponse postUser(
            Session session,
            UserModel userModel) {
        session.setRoleType(RoleType.ADMIN);
        System.out.println(userModel.getUserName());
        System.out.println(userModel.getUserPass());
        return new PianaResponse(Response.Status.OK, 1,
                userModel,
                MediaType.APPLICATION_JSON,
                Charset.forName("UTF-8"));
    }

    public static PianaResponse postUser(
            Session session,
            Map<String, List<String>> mapParams,
            UserModel userModel) {
        System.out.println(userModel.getUserName());
        System.out.println(userModel.getUserPass());
        return new PianaResponse(Response.Status.OK, 1,
                userModel,
                MediaType.APPLICATION_JSON,
                Charset.forName("UTF-8"));
    }

    public static PianaResponse getUser(
            Session session,
            Map<String, List<String>> mapParams) {
        session.setRoleType(RoleType.ADMIN);
        return new PianaResponse(Response.Status.OK, 1,
                "get user",
                MediaType.TEXT_PLAIN,
                Charset.forName("UTF-8"));
    }
}
