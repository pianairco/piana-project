package ir.piana.dev.server.sample;

import ir.piana.dev.server.PianaAppMain;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;
import org.junit.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.io.InputStream;

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

//    @AfterClass
//    public static void afterClass()
//            throws Exception {
//        PianaAppMain.stopHttpServer();
//    }

    @Before
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Test
    public void testPianaWebtool() {
        WebTarget target = client.target(BASE_URI);
        javax.ws.rs.core.Response response = target.path("hello-world")
                .request().get();
        String hello = response
                .readEntity(String.class);
        Assert.assertEquals("Hello World", hello);
        response.close();
    }

    public static PianaResponse getHelloWorld(
            Session session
    ) {
        return new PianaResponse(
                Status.OK,
                "Hello World",
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getHelloToName(
            Session session,
            String name
    ) {
        return new PianaResponse(Status.OK,
                "hello ".concat(name),
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getHelloToNameFamily(
            Session session,
            String name,
            String family
    ) {
        return new PianaResponse(Status.OK,
                "hello ".concat(name)
                        .concat(" ")
                        .concat(family),
                MediaType.TEXT_PLAIN);
    }

    public static PianaResponse getMessageToNameFamily(
            Session session,
            String name,
            String family,
            String message
    ) {
        return new PianaResponse(Status.OK,
                message.concat(" ")
                        .concat(name)
                        .concat(" ")
                        .concat(family),
                MediaType.TEXT_PLAIN);
    }
}
