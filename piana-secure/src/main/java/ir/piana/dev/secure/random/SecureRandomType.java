package ir.piana.dev.secure.random;

/**
 * @author Mohammad Rahmati, 4/15/2017 10:06 AM
 */
public enum SecureRandomType {
    SHA_1_PRNG("SHA1PRNG");

    private String name;

    private SecureRandomType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
