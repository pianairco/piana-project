package ir.piana.dev.server.document;

import java.util.List;

/**
 * Created by SYSTEM on 7/19/2017.
 */
public class ServicesModel {
    private String resourcePath;
    private String idName;
    private Boolean asset;
    private String returnType;
    private List<String> pathParams;
    private List<String> queryParams;
    private String methodType;

    public ServicesModel() {
    }

    public ServicesModel(String resourcePath, String idName, Boolean asset, String returnType, List<String> pathParams, List<String> queryParams, String methodType) {
        this.resourcePath = resourcePath;
        this.idName = idName;
        this.asset = asset;
        this.returnType = returnType;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
        this.methodType = methodType;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public List<String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(List<String> pathParams) {
        this.pathParams = pathParams;
    }

    public List<String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(List<String> queryParams) {
        this.queryParams = queryParams;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getIdName() {
        return idName;
    }

    public void setIdName(String idName) {
        this.idName = idName;
    }

    public Boolean getAsset() {
        return asset;
    }

    public void setAsset(Boolean asset) {
        this.asset = asset;
    }
}
