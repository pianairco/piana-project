package ir.piana.dev.secure.key;

/**
 * @author Mohammad Rahmati, 4/16/2017 4:27 PM
 */
public enum SecretKeyAlgorithm {
    DES("DES", "DES", 56),
    DES_EDE_128("DESede_128", "DESede", 112),
    DES_EDE_192("DESede_192", "DESede", 168),
    AES_128("AES_128", "AES", 128),
    AES_192("AES_192", "AES", 192),
    AES_256("AES_256", "AES", 256);

    private String name;
    private String jceName;
    private int keySize;

    private SecretKeyAlgorithm(
            String name, String jceName, int keySize) {
        this.name = name;
        this.jceName = jceName;
        this.keySize = keySize;
    }

    public String getName() {
        return name;
    }

    public String getJceName() {
        return jceName;
    }

    public int getKeySize() {
        return keySize;
    }
}
