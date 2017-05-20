package ir.piana.dev.secure.key;

/**
 * @author Mohammad Rahmati, 4/24/2017 2:08 PM
 */
public enum KeyPairAlgorithm {
    RSA_1024("rsa_1024", "RSA", 1024),
    RSA_2048("rsa_2048", "RSA", 2048),
    RSA_3072("rsa_3072", "RSA", 3072),
    RSA_4048("rsa_4048", "RSA", 4048),
    DSA_1024("dsa_1024", "DSA", 1024),
    DSA_2048("dsa_2048", "DSA", 2048);

    private String name;
    private String jceName;
    private int keySize;

    private KeyPairAlgorithm(
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
