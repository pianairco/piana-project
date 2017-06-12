package ir.piana.dev.sample.rest;

import ir.piana.dev.sample.SampleAppMain;
import ir.piana.dev.sample.model.UserModel;
import org.glassfish.jersey.client.ClientConfig;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * @author Mohammad Rahmati, 6/12/2017 7:16 AM
 */
public class AuthRestTest {
    @BeforeClass
    public static void initClass()
            throws Exception {
        SampleAppMain.startApp();
    }

    @AfterClass
    public static void terminateClass()
            throws Exception {
        SampleAppMain.stopApp();
    }

    @Test
    public void testPostUserGetUserModel() {
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(
                UriBuilder.fromUri(
                        "http://localhost:8001/"));

        UserModel userModel = target.path("auth").
                request().
                accept(MediaType.APPLICATION_JSON).
                post(Entity.entity(new UserModel("ali", "123"),
                        MediaType.APPLICATION_JSON), UserModel.class);

//        String htmlAnswer=
//                target.path("rest").path("hello").request().accept(MediaType.TEXT_HTML).get(String.class);

        Assert.assertEquals("name is incorrect!",
                "ali", userModel.getUserName());
    }

    @Test
    public void testPostUserGetResponse() {
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(
                UriBuilder.fromUri(
                        "http://localhost:8001/"));

        Response response = target.path("auth").
                request().
                accept(MediaType.APPLICATION_JSON).
                post(Entity.entity(new UserModel("ali", "123"),
                        MediaType.APPLICATION_JSON));

        UserModel userModel = response
                .readEntity(UserModel.class);
        Assert.assertEquals("name is incorrect!",
                "ali", userModel.getUserName());
    }
}
