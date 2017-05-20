package ir.piana.dev.server.session;

import ir.piana.dev.secure.crypto.CryptoAttribute;
import ir.piana.dev.secure.crypto.CryptoMaker;
import ir.piana.dev.server.role.RoleType;

import java.security.KeyPair;

/**
 * @author Mohammad Rahmati, 4/18/2017 4:30 PM
 */
public class Session {
    private KeyPair keyPair;
    private RoleType roleType;
    private String sessionKey;

    Session(KeyPair keyPair,
            RoleType roleType) {
        this.keyPair = keyPair;
        this.roleType = roleType;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public byte[] getPublicKeyBytes() {
        return keyPair.getPublic().getEncoded();
    }

    public byte[] decrypt(byte[] rawMessage)
            throws Exception {
        return CryptoMaker.decrypt(rawMessage,
                keyPair.getPrivate(),
                CryptoAttribute.RSA);
    }
}
