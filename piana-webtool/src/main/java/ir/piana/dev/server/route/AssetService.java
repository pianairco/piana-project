package ir.piana.dev.server.route;

import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.asset.PianaAssetResolver;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

/**
 * @author Mohammad Rahmati, 5/10/2017 8:46 PM
 */
class AssetService {
    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver) {
        PianaAsset asset = assetResolver
                .resolve("index.html");
        return new PianaResponse(
                javax.ws.rs.core.Response.Status.OK,
                asset.getBytes(),
                asset.getMediaType());
    }

    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver,
            String path) {
        if(path == null || path.isEmpty())
            return getAsset(session, assetResolver);
        PianaAsset asset = assetResolver
                .resolve(path);
        return new PianaResponse(
                javax.ws.rs.core.Response.Status.OK,
                asset.getBytes(),
                asset.getMediaType());
    }
}
