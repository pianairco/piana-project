package ir.piana.dev.sample.rest;

import ir.piana.dev.sample.model.UserModel;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;

/**
 * @author Mohammad Rahmati, 6/3/2017 1:40 PM
 */
public class AuthRest {
    public static PianaResponse postUser(
            Session session,
            UserModel userModel) {
        System.out.println(userModel.getUserName());
        System.out.println(userModel.getUserPass());
        return new PianaResponse(Response.Status.OK,
                userModel,
                MediaType.APPLICATION_JSON,
                Charset.forName("UTF-8"));
    }
}
