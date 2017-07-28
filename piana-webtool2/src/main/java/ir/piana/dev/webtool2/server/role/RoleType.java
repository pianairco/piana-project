package ir.piana.dev.webtool2.server.role;

/**
 * @author Mohammad Rahmati, 4/22/2017 2:41 PM
 */
public enum RoleType {
    ADMIN("ADMIN", (byte)0xFF),
    USER("USER", (byte)0x07),
    GUEST("GUEST", (byte)0x03),
    NEEDLESS("NEEDLESS", (byte)0x01);

    private String name;
    private byte scheme;

    RoleType(
            String name, byte scheme) {
        this.name = name;
        this.scheme = scheme;
    }

    public String getName() {
        return name;
    }

    public int getScheme() {
        return scheme;
    }

    public static RoleType getFromName(String name)
            throws Exception {
        if(name == null || name.isEmpty())
            return NEEDLESS;
        for (RoleType ruleType : RoleType.values()) {
            if(ruleType.name.equalsIgnoreCase(name))
                return ruleType;
        }
        throw new Exception("this type not exist");
    }

    public boolean isValid(RoleType ruleType) {
        if((scheme & ruleType.scheme) != scheme)
            return false;
        return true;
    }
}
