package ir.piana.dev.webtool2.server.http;

/**
 * @author Mohammad Rahmati, 4/23/2017 12:24 PM
 */
public enum HttpServerType {
    NETTY("netty"),
    JETTY("jetty"),
    UNKNOWN("unknown");

    private String name;

    HttpServerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static HttpServerType fromName(
            String name) {
        if(name == null || name.isEmpty())
            return UNKNOWN;
        for(HttpServerType s
                : HttpServerType.values()) {
            if(s.name.equalsIgnoreCase(name))
                return s;
        }
        return UNKNOWN;
    }
}
