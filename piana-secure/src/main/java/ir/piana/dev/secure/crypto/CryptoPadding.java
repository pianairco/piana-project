package ir.piana.dev.secure.crypto;

/**
 * @author Mohammad Rahmati, 4/16/2017 4:27 PM
 */
public enum CryptoPadding {
    PKCS_5_PADDING("pkcs5padding", "PKCS5PADDING"),
    PKCS_7_PADDING("pkcs7padding", "PKCS5PADDING");

    private String name;
    private String jceName;

    private CryptoPadding(
            String name, String jceName) {
        this.name = name;
        this.jceName = jceName;
    }

    public String getName() {
        return name;
    }

    public String getJceName() {
        return jceName;
    }
}
