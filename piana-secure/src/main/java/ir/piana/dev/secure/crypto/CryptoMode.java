package ir.piana.dev.secure.crypto;

/**
 * @author Mohammad Rahmati, 4/16/2017 4:27 PM
 */
public enum CryptoMode {
    ECB("ecb", "ECB"),
    CBC("cbc", "CBC"),
    CFB("cfb", "CFB"),
    OFB("ofb", "OFB");

    private String name;
    private String jceName;

    private CryptoMode(
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
