package ir.piana.dev.server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

/**
 * @author Mohammad Rahmati, 5/3/2017 7:59 AM
 */
public class PianaServiceConfig {
    protected ObjectNode objectNode = null;
    private List<String> urls = null;
    private Map<String, List<String>> urlToMethodsMap =
            new LinkedHashMap<>();
    protected Map<String, Map<String, ObjectNode>>
            urlToMethodObjectsMap = new LinkedHashMap<>();

    protected PianaServiceConfig(JsonNode jsonNode)
            throws Exception {
        if(!(jsonNode instanceof ObjectNode))
            throw new Exception("node not is ObjectNode");
        this.objectNode = (ObjectNode) jsonNode;
    }

    public List<String> getUrls() {
        if(urls != null)
            return urls;
        urls = new ArrayList<>();
        Iterator<String> stringIterator = objectNode
                .fieldNames();
        stringIterator.forEachRemaining(url -> {
            urls.add(url);
        });
        return urls;
    }

    public List<String> getMethods(String url)
            throws Exception {
        List<String> urls = getUrls();
        if(!urls.contains(url))
            throw new Exception("this url not exist");
        if(urlToMethodsMap.containsKey(url))
            return urlToMethodsMap.get(url);
        JsonNode urlObject = objectNode.get(url);
        if(!(urlObject instanceof ObjectNode))
            throw new Exception("url not is ObjectNode");
        Iterator<String> methodIterator = ((ObjectNode) urlObject)
                .fieldNames();
        List<String> methodList = new ArrayList<>();
        methodIterator.forEachRemaining(methodName -> {
            methodList.add(methodName);
            urlToMethodObjectsMap.put(url,
                    new LinkedHashMap<>());
        });
        urlToMethodsMap.put(url, methodList);
        return methodList;
    }

    public ObjectNode getMethodObject(
            String url,
            String method)
            throws Exception {
        if(!getMethods(url).contains(method))
            throw new Exception("this method not exist");
        Map<String, ObjectNode> methodMap =
                urlToMethodObjectsMap.get(url);
        if(methodMap.containsKey(method))
            return methodMap.get(method);
        JsonNode jsonNode = objectNode.get(url).get(method);
        if(!(jsonNode instanceof ObjectNode))
            throw new Exception("node not is ObjectNode");
        methodMap.put(method, (ObjectNode)jsonNode);
        return methodMap.get(method);
    }
}
