package ir.piana.dev.server.config;

import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/3/2017 7:59 AM
 */
public class PianaConfig {
    protected Map<String, Object> configMap = null;

    protected PianaConfig() {}

    protected PianaConfig(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    protected String getString(String key) {
        if(configMap == null)
            return null;
        Object o = configMap.get(key);
        if(o instanceof String)
            return (String)o;
        return null;
    }

    protected List<String> getList(String key) {
        if(configMap == null)
            return null;
        Object o = configMap.get(key);
        if(o instanceof List)
            return (List<String>) o;
        return null;
    }

    protected Map<String, Object> getMap(String key) {
        if(configMap == null)
            return null;
        Object o = configMap.get(key);
        if(o instanceof Map)
            return (Map<String, Object>) o;
        return null;
    }

    protected PianaConfig getShbConfig(String key) {
        if(configMap == null)
            return null;
        Object o = configMap.get(key);
        if(o instanceof Map)
            return new PianaConfig((Map<String, Object>) o);
        return null;
    }

    public void reconfigure(PianaConfig pianaConfig) {
        this.configMap = pianaConfig.configMap;
    }

    public void reconfigure(Map<String, Object> configMap) {
        this.configMap = configMap;
    }
}
