package ir.piana.dev.webtool2.server.route;

import ir.piana.dev.webtool2.server.asset.PianaAsset;
import ir.piana.dev.webtool2.server.asset.PianaAssetResolver;
import ir.piana.dev.webtool2.server.response.PianaResponse;
import ir.piana.dev.webtool2.server.session.Session;

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
                Status.OK, 0,
                asset.getBytes(),
                asset.getMediaType());
    }

    //called at all condition
    public static PianaResponse getAsset(
            Session session,
            PianaAssetResolver assetResolver,
            String filePath) {
        if(filePath == null || filePath.isEmpty())
            return getAsset(session,
                    assetResolver);
        else {
            PianaAsset asset = null;
            try {
                asset = assetResolver
                        .resolve(filePath);
            } catch (Exception e) {
                return notFoundResponse();
            }
            return new PianaResponse(
                    Status.OK,
                    0,
                    asset.getBytes(),
                    asset.getMediaType(),
                    Charset.forName("UTF-8"));
        }
    }

    protected static PianaResponse notFoundResponse() {
        return new PianaResponse(
                Status.NOT_FOUND,
                0,
                "not found asset",
                MediaType.TEXT_PLAIN);
    }
}
