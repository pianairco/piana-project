package ir.piana.dev.webtool2.server.response;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import java.nio.charset.Charset;

/**
 * @author Mohammad Rahmati, 5/10/2017 10:52 AM
 */
public class PianaResponse {
    private Status responseStatus;
    private int status;
    private Object entity;
    private String mediaType;
    private Charset charset;

    public PianaResponse() {
        this(Status.NO_CONTENT,
                0,
                null,
                MediaType.APPLICATION_JSON,
                Charset.forName("ASCII"));
    }

    public PianaResponse(Status responseStatus,
                         Object entity) {
        this(responseStatus, 0, entity,
                MediaType.APPLICATION_JSON,
                Charset.forName("ASCII"));
    }

    public PianaResponse(Status responseStatus,
                         int status,
                         Object entity) {
        this(responseStatus, status, entity,
                MediaType.APPLICATION_JSON,
                Charset.forName("ASCII"));
    }

    public PianaResponse(Status responseStatus,
                         int status,
                         Object entity,
                         String mediaType) {
        this(responseStatus,
                status,
                entity,
                mediaType,
                Charset.forName("ASCII"));
    }

    public PianaResponse(Status responseStatus,
                         int status,
                         Object entity,
                         String mediaType,
                         Charset charset) {
        this.responseStatus = responseStatus;
        this.status = status;
        this.entity = entity;
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public Status getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Status responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Object getEntity() {
        return entity;
    }

    public void setEntity(Object entity) {
        this.entity = entity;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
