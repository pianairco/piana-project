package ir.piana.dev.server.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.piana.dev.server.config.PianaRouterConfig.PianaRouteConfig;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/3/2017 8:01 AM
 */
public class PianaConfigReader {
    private static final Logger logger =
            Logger.getLogger(
                    PianaConfigReader.class);

    public static JsonNode createJsonNode(
            InputStream inputStream)
            throws Exception {
        if(inputStream == null)
            throw new Exception("input stream is null");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(
                    inputStream);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return jsonNode;
    }

    public static PianaConfig createFromJson(
            String configFilePath)
            throws Exception {
        return createFromJson(
                new FileInputStream(
                        new File(configFilePath)));
    }

    public static PianaConfig createFromJson(
            File configFile)
            throws Exception {
        return createFromJson(new FileInputStream(configFile));
    }

    public static PianaConfig createFromJson(
            InputStream inputStream)
            throws Exception {
        if(inputStream == null)
            throw new Exception("input stream is null");

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        bis.mark(0);
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

        PianaConfig pianaConfig =
                new PianaConfig();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.getFactory().disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
            pianaConfig.jsonNode = objectMapper.readTree(bis);
            bis.reset();
            pianaConfig.configMap = objectMapper
                    .readValue(
                            bis, Map.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return pianaConfig;
    }

    public static PianaConfig createFromJsonNode(
            InputStream inputStream)
            throws Exception {
        if(inputStream == null)
            throw new Exception("input stream is null");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PianaConfig pianaConfig = new PianaConfig(
                    objectMapper.readTree(inputStream));
            return pianaConfig;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new PianaConfig();
    }

    public static PianaConfig createFromMap(
            Map<String, Object> configMap)
            throws Exception {
        if(configMap == null)
            throw new Exception("input map is null");

        PianaConfig pianaConfig =
                new PianaConfig();
        pianaConfig.configMap = configMap;
        return pianaConfig;
    }

    public static PianaServiceConfig createPianaServiceConfig(
            InputStream inputStream)
            throws Exception {
        if(inputStream == null)
            throw new Exception("input stream is null");

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PianaServiceConfig config = new PianaServiceConfig(
                    objectMapper.readTree(inputStream));
            return config;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw e;
        }
    }

    public static PianaServiceConfig createPianaServiceConfig(
            JsonNode jsonNode)
            throws Exception {
        if(jsonNode == null)
            throw new Exception("input json node is null");

        PianaServiceConfig config = new PianaServiceConfig(
                    jsonNode);
            return config;
    }

    public static PianaRouteConfig createPianaRouteConfig(
            JsonNode jsonNode)
            throws Exception {
        if(jsonNode == null)
            throw new Exception("input json node is null");

        PianaRouteConfig config = new PianaRouteConfig();
        config.reconfigure(new PianaConfig(jsonNode));
        return config;
    }
}
