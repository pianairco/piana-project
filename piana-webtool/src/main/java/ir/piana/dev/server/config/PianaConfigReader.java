package ir.piana.dev.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/3/2017 8:01 AM
 */
public class PianaConfigReader {
    private static final Logger logger =
            Logger.getLogger(
                    PianaConfigReader.class);

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

        PianaConfig pianaConfig =
                new PianaConfig();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            pianaConfig.configMap = objectMapper
                    .readValue(
                            inputStream, Map.class);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return pianaConfig;
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
}
