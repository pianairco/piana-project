package ir.piana.dev.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 5/3/2017 7:59 AM
 */
public class PianaConfig {
    protected Map<String, Object> configMap = null;
    protected JsonNode jsonNode = null;

    protected PianaConfig() {}

    protected PianaConfig(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    protected PianaConfig(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    protected PianaConfig(JsonNode jsonNode, Map<String, Object> configMap) {
        this.jsonNode = jsonNode;
        this.configMap = configMap;
    }

    protected String getString(String key) {
        if(configMap == null && jsonNode == null)
            return null;
        else if (jsonNode != null &&
                jsonNode instanceof ObjectNode) {
            return jsonNode.get(key) == null ? null :
                    jsonNode.get(key).textValue();
        } else {
            Object o = configMap.get(key);
            if(o != null && o instanceof String)
                return (String)o;
        }
        return null;
    }

    protected List<String> getList(String key) {
        if(configMap == null && jsonNode == null)
            return null;
        else if(jsonNode != null &&
                jsonNode instanceof ObjectNode) {
            JsonNode jsonNode = this.jsonNode.get(key);
            List<String> list = new ArrayList<>();
            if(jsonNode instanceof ArrayNode)
                ((ArrayNode)jsonNode).forEach(node ->
                        list.add(node.textValue())
                );
            return list;
        } else {
            Object o = configMap.get(key);
            if (o instanceof List)
                return (List<String>) o;
        }
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

    protected JsonNode getJsonNode(String key) {
        if(jsonNode == null)
            return null;
        if(jsonNode instanceof ObjectNode)
            return ((ObjectNode)jsonNode).get(key);
        return null;
    }

    protected PianaConfig getPianaConfig(String key) {
        if(configMap == null && jsonNode == null)
            return null;
        JsonNode newNode = null;
        if(jsonNode != null) {
            newNode = jsonNode.get(key);
        }
        Map<String, Object> newMap = null;
        if(configMap != null) {
            Object o = configMap.get(key);
            if (o != null && o instanceof Map)
                newMap = (Map<String, Object>) o;
        }
        if(newNode != null && newMap != null)
            return new PianaConfig(newNode, newMap);
        else if(newNode != null)
            return new PianaConfig(newNode);
        else if(newMap != null)
            return new PianaConfig(newMap);
        return null;
    }

    protected void reconfigure(PianaConfig pianaConfig) {
        if(pianaConfig.jsonNode != null)
            this.jsonNode = pianaConfig.jsonNode;
        if(pianaConfig != null)
            this.configMap = pianaConfig.configMap;
    }

    protected void reconfigure(Map<String, Object> configMap) {
        this.configMap = configMap;
    }

    protected void reconfigure(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }
}
