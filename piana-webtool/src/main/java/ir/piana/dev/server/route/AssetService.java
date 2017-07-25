package ir.piana.dev.server.route;

import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.asset.PianaAssetResolver;
import ir.piana.dev.server.response.PianaResponse;
import ir.piana.dev.server.session.Session;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/10/2017 8:46 PM
 */
class AssetService {
    //called if path is null or empty
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

    //called at all condition
    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver,
            Map<String, List<String>> map) {
        String path = null;
        if(map != null && !map.isEmpty() && (path = map.get(map.keySet().toArray()[0]).get(0)) != null)
            return getAsset(session,
                    assetResolver,
                    path,
                    map);
        else {
            return getAsset(session, assetResolver);
        }
    }

    //called if path is exist
    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver,
            String path,
            Map<String, List<String>> map) {
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
                asset.getMediaType(),
                Charset.forName("UTF-8"));
    }

    protected static PianaResponse notFoundResponse() {
        return new PianaResponse(
                Status.NOT_FOUND,
                "not found asset",
                MediaType.TEXT_PLAIN);
    }
}
