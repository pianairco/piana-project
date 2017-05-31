package ir.piana.dev.server.route;

import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.asset.PianaAssetResolver;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

/**
 * @author Mohammad Rahmati, 5/10/2017 8:46 PM
 */
class AssetService {
    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver) {
        PianaAsset asset = null;
        try {
            asset = assetResolver
                    .resolve("index.html");
        } catch (Exception e) {
            return notFoundResponse();
        }
        return new PianaResponse(
                Status.OK,
                asset.getBytes(),
                asset.getMediaType());
    }

    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver,
            String path) {
        if(path == null || path.isEmpty())
            return getAsset(session, assetResolver);
        PianaAsset asset = null;
        try {
            asset = assetResolver
                    .resolve(path);
        } catch (Exception e) {
            return notFoundResponse();
        }
        return new PianaResponse(
                Status.OK,
                asset.getBytes(),
                asset.getMediaType());
    }

    protected static PianaResponse notFoundResponse() {
        return new PianaResponse(
                Status.NOT_FOUND,
                "not found asset",
                MediaType.TEXT_PLAIN);
    }
}