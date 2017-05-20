package ir.piana.dev.secure.crypto;

/**
 * @author Mohammad Rahmati, 4/16/2017 4:27 PM
 */
public enum CryptoAlgorithm {
    DES("DES", "DES"),
    DES_EDE("DESede", "DESede"),
    AES("AES", "AES"),
    RSA("RSA", "RSA");

    private String name;
    private String jceName;
//    private int keySize;

    private CryptoAlgorithm(
            String name, String jceName/*, int keySize*/) {
        this.name = name;
        this.jceName = jceName;
//        this.keySize = keySize;
    }

    public String getName() {
        return name;
    }

    public String getJceName() {
        return jceName;
    }

//    public int getKeySize() {
//        return keySize;
//    }
}
