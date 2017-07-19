package ir.piana.dev.server.route;

import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaServerConfig;
import ir.piana.dev.server.document.DocumentResolver;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Created by SYSTEM on 7/17/2017.
 */
class DocumentService {
    protected static PianaRouterConfig routerConfig;
    protected static PianaServerConfig serverConfig;
    protected static String documentStartUrl;

    public static PianaResponse getPianaDocument(
            Session session,
            Map<String, List<String>> map) {
        PianaAsset asset = null;
        try {
            asset = DocumentResolver
                    .getPianaDocumentHtml(routerConfig, serverConfig, documentStartUrl);
        } catch (Exception e) {
            return notFoundResponse();
        }
        return new PianaResponse(
                Response.Status.OK,
                asset.getBytes(),
                MediaType.TEXT_HTML);
    }

    public static PianaResponse getPianaJson(
            Session session,
            Map<String, List<String>> map) {
        PianaAsset asset = null;
        try {
            asset = DocumentResolver
                    .getPianaDocumentJsonModel(routerConfig, serverConfig, documentStartUrl);
        } catch (Exception e) {
            return notFoundResponse();
        }
        return new PianaResponse(
                Response.Status.OK,
                asset.getBytes(),
                MediaType.APPLICATION_JSON);
    }

    protected static PianaResponse notFoundResponse() {
        return new PianaResponse(
                Response.Status.NOT_FOUND,
                "not found asset",
                MediaType.TEXT_PLAIN);
    }
}
