package ir.piana.dev.server.sample;

import ir.piana.dev.server.PianaAppMain;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/15/2017 4:21 PM
 */
public class SampleServiceTest {
    private Client client;
    static final String BASE_URI =
            "http://localhost:8001/";

    @BeforeClass
    public static void beforeClass()
            throws Exception {
        InputStream serverConfigStream = SampleServiceTest
                .class.getResourceAsStream(
                "/piana-server-config.json");
        InputStream routeConfigStream = SampleServiceTest
                .class.getResourceAsStream(
                "/piana-route-config.json");

        PianaAppMain.startHttpServer(serverConfigStream,
                routeConfigStream);
    }

    @AfterClass
    public static void afterClass()
            throws Exception {
        PianaAppMain.stopHttpServer();
    }

    @Before
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void testHelloWorld() {
        WebTarget target = client.target(BASE_URI);
        javax.ws.rs.core.Response response =
                target.path("hello-world")
                        .request().get();
        String hello = response
                .readEntity(String.class);
        Assert.assertEquals("Hello World", hello);
        response.close();
    }

    @Test
    public void testHelloToName() {
        WebTarget target = client.target(BASE_URI);
        javax.ws.rs.core.Response response =
                target.path("hello-world/ali")
                        .request().get();
        Assert.assertEquals(401, response.getStatus());
        response.close();
    }

    @Test
    public void testMessageToNameFamily() {
        WebTarget target = client.target(BASE_URI);
        javax.ws.rs.core.Response response =
                target.path("hello-world/ali/ahmadi")
                        .queryParam("message", "hi")
                        .request().get();
        String message = response
                .readEntity(String.class);
        Assert.assertEquals("hi ali ahmadi", message);
        response.close();
    }

    public static PianaResponse getHelloWorld(
            Session session,
            Map<String, List<String>> parameters) {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PianaResponse(
                Status.OK,
                "Hello World",
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getHelloToName(
            Session session,
            Map<String, List<String>> parameters
    ) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PianaResponse(Status.OK,
                "Hello ".concat(parameters.get("name").get(0)),
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getMessageToNameFamily(
            Session session,
            Map<String, List<String>> parameters
    ) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PianaResponse(Status.OK,
                parameters.get("message").get(0)
                        .concat(" ")
                        .concat(parameters.get("name").get(0))
                        .concat(" ")
                        .concat(parameters.get("family").get(0)),
                MediaType.TEXT_PLAIN);
    }
}
