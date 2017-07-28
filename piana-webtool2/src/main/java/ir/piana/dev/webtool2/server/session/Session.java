package ir.piana.dev.webtool2.server.session;

import ir.piana.dev.secure.crypto.CryptoAttribute;
import ir.piana.dev.secure.crypto.CryptoMaker;
import ir.piana.dev.webtool2.server.role.RoleType;

import java.security.KeyPair;
import java.util.*;

/**
 * @author Mohammad Rahmati, 4/18/2017 4:30 PM
 */
public class Session {
    private boolean wrongdoer;
    private String sessionName;
    private KeyPair keyPair;
    private RoleType roleType;
    private String sessionKey;
    private Map<String, String> stringMap;
    private Map<String, Object> objectMap;

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
        this.stringMap = new LinkedHashMap<>();
        this.objectMap = new LinkedHashMap<>();
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

    public void setString(String key, String value) {
        this.stringMap.put(key, value);
    }

    public String getString(String key) {
        return this.stringMap.get(key);
    }

    public String removeString(String key) {
        return this.stringMap.remove(key);
    }

    public void clearString() {
        this.stringMap.clear();
    }

    public void setObject(String key, Object value) {
        this.objectMap.put(key, value);
    }

    public Object getObject(String key) {
        return this.objectMap.get(key);
    }

    public Object removeObject(String key) {
        return this.objectMap.remove(key);
    }

    public void clearObject() {
        this.objectMap.clear();
    }

    public byte[] decrypt(byte[] rawMessage)
            throws Exception {
        return CryptoMaker.decrypt(rawMessage,
                keyPair.getPrivate(),
                CryptoAttribute.RSA);
    }

    public Boolean isWrongdoer() {
        return wrongdoer;
    }

    public void setWrongdoer(Boolean wrongdoer) {
        this.wrongdoer = wrongdoer;
    }
}
