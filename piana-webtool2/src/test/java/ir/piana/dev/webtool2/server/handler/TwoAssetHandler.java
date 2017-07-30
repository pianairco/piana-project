package ir.piana.dev.webtool2.server.handler;

import ir.piana.dev.webtool2.server.annotation.AssetHandler;
import ir.piana.dev.webtool2.server.annotation.Handler;
import ir.piana.dev.webtool2.server.annotation.HandlerType;

/**
 * Created by SYSTEM on 7/30/2017.
 */
@Handler(baseUrl = "/", handlerType = HandlerType.ASSET_HANDLER)
@AssetHandler(assetPath = "./react-client")
public class TwoAssetHandler {
}
