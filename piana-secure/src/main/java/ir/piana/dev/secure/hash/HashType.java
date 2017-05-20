package ir.piana.dev.secure.hash;

/**
 * @author Mohammad Rahmati, 4/15/2017 10:06 AM
 */
public enum HashType {
    MD_2("MD2"),
    MD_5("MD5"),
    SHA("SHA"),
    SHA_224("SHA-224"),
    SHA_256("SHA-256"),
    SHA_384("SHA-384"),
    SHA_512("SHA-512");

    private String name;

    private HashType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
