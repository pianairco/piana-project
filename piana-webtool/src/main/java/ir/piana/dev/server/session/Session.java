package ir.piana.dev.server.session;

import ir.piana.dev.secure.crypto.CryptoAttribute;
import ir.piana.dev.secure.crypto.CryptoMaker;
import ir.piana.dev.server.role.RoleType;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import java.security.KeyPair;
import java.util.*;

/**
 * @author Mohammad Rahmati, 4/18/2017 4:30 PM
 */
public class Session {
    private String sessionName;
    private KeyPair keyPair;
    private RoleType roleType;
    private String sessionKey;
    private Map<String, String> sessionMap;

    Session(String sessionName,
            KeyPair keyPair,
            RoleType roleType) {
        this(sessionName, keyPair, roleType, null);
    }

    Session(String sessionName,
            KeyPair keyPair,
            RoleType roleType,
            String sessionKey) {
        this.sessionName = sessionName;
        this.keyPair = keyPair;
        this.roleType = roleType;
        this.sessionKey = sessionKey;
        this.sessionMap = new LinkedHashMap<>();
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

    public void set(String key, String value) {
        this.sessionMap.put(key, value);
    }

    public String get(String key) {
        return this.sessionMap.get(key);
    }

    public String remove(String key) {
        return this.sessionMap.remove(key);
    }

    public void clear() {
        this.sessionMap.clear();
    }

    public byte[] decrypt(byte[] rawMessage)
            throws Exception {
        return CryptoMaker.decrypt(rawMessage,
                keyPair.getPrivate(),
                CryptoAttribute.RSA);
    }
}
