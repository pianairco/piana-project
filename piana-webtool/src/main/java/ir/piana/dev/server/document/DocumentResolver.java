package ir.piana.dev.server.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.server.asset.PianaAsset;
import ir.piana.dev.server.asset.PianaAssetResolver;
import ir.piana.dev.server.config.PianaRouterConfig;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import ir.piana.dev.server.config.PianaServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by SYSTEM on 7/17/2017.
 */
public abstract class DocumentResolver {
    private static Logger logger = Logger.getLogger(
            DocumentResolver.class);
    private static PianaAsset documentHtml = null;
    private static String rootPath;
    private static PianaAssetResolver assetResolver;

    public static PianaAsset getPianaDocumentHtml(
            PianaRouterConfig routerConfig,
            PianaServerConfig serverConfig,
            String documentStartUrl)
            throws Exception {
        if(documentHtml == null) {
            InputStream is = DocumentResolver.class.getResourceAsStream("/document.html");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String res;
            while (reader.ready()){
                res = reader.readLine();
                int i = StringUtils.countMatches(res, "@");
                if (i >= 2) {
                    int first = StringUtils.indexOf(res, "@");
                    int second = StringUtils.indexOf(res, "@", first + 1);
                    String substring = StringUtils.substring(res, first, second + 1);
                    String removed = StringUtils.remove(substring, "@");
                    res = StringUtils.replace(res, substring, "http://"
                            .concat(serverConfig.getIP())
                            .concat(":").concat(serverConfig.getPort())
                            .concat("/").concat(documentStartUrl)
                            .concat("/json-model"));
                }
                sb.append(res.concat("\n"));
            }
            documentHtml = new PianaAsset(sb.toString().getBytes(),
                    null, null, MediaType.TEXT_HTML);
        }

        return documentHtml;
    }

    public static PianaAsset getPianaDocumentJsonModel(
            PianaRouterConfig routerConfig,
            PianaServerConfig serverConfig,
            String documentStartUrl)
            throws Exception {
        List<ServicesModel> servicesModels = new ArrayList<>();
        Set<String> urlPatterns = routerConfig.getUrlPatterns();
        urlPatterns.stream().forEach(urlPattern -> {
            System.out.println(urlPattern);
            Set<String> httpMethodPatterns = routerConfig.getHttpMethodPatterns(urlPattern);
            httpMethodPatterns.stream().forEach(methodPattern -> {
                System.out.println(methodPattern);
                PianaRouteConfig routeConfig = routerConfig
                        .getRouteConfig(urlPattern, methodPattern);
                ServicesModel servicesModel = new ServicesModel();
                String methodType = methodPattern.contains("#") ?
                        methodPattern.substring(0, methodPattern.indexOf('#')) : methodPattern;
                servicesModel.setMethodType(methodType);
                String pathParamStrings = methodPattern.contains("#") ?
                        methodPattern.substring(methodPattern.indexOf('#') + 1) : null;
                String[] split = null;
                if(pathParamStrings != null) {
                    split = pathParamStrings.replaceAll("\\*", "").split(":");
                    servicesModel.setPathParams(Arrays.asList(split));
                }
                else {
                    servicesModel.setPathParams(null);
                }
                servicesModel.setQueryParams(routeConfig.getQueryParams());
                servicesModel.setResourcePath("http://"
                        .concat(serverConfig.getIP()).concat(":")
                        .concat(serverConfig.getPort())
                        .concat(urlPattern));
                String idName = methodPattern.concat(urlPattern);
                if(pathParamStrings != null)
                    idName.concat(pathParamStrings);
                servicesModel.setAsset(routeConfig.isAsset());
                servicesModel.setIdName(idName.replaceAll("/", "")
                        .replaceAll(":", "").replaceAll("#", "")
                        .replace("-", "").replaceAll("\\*","").toLowerCase());
                servicesModels.add(servicesModel);
            });
        });

        ObjectMapper objectMapper = new ObjectMapper();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        String routerJsonString = null;
        byte[] data = null;
        try {
            mapper.writeValue(out, servicesModels);
            data = out.toByteArray();
//            routerJsonString = objectMapper.writeValueAsString(
//                    routerConfig.getJsonNode());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new PianaAsset(data, null, null,
                MediaType.APPLICATION_JSON);
    }
}
