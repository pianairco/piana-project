package ir.piana.dev.webtool2.server.asset;

import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * @author Mohammad Rahmati, 4/19/2017 1:27 PM
 */
public class PianaAsset {
    private byte[] bytes;
    private String relativePath;
    private String rootPath;
    private String path;
    private String mediaType;

    public PianaAsset(
            byte[] bytes,
            String rootPath,
            String relativePath) {
        this(bytes, relativePath, rootPath,
                MediaType.TEXT_PLAIN);
    }

    public PianaAsset(
            byte[] bytes,
            String rootPath,
            String relativePath,
            String mediaType) {
        this.bytes = bytes;
        this.relativePath = relativePath;
        this.rootPath = rootPath;
        this.mediaType = mediaType;
        try {
            this.path = rootPath.concat(File.pathSeparator)
                    .concat(relativePath);
        } catch (Exception e) {
            this.path = null;
        }
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getPath() {
        return path;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
