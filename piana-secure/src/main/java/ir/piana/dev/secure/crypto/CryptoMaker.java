package ir.piana.dev.secure.crypto;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 4/16/2017 6:24 PM
 */
public class CryptoMaker {
    private static boolean initialized = false;
    private static final
    Map<String, Cipher> cipherMap
            = new HashMap<>();

    private static synchronized void initialize()
            throws Exception {
        if (initialized)
            return;
        for (CryptoAttribute cryptoAttribute
                : CryptoAttribute.values()) {
            try {
                Cipher cipher = Cipher.getInstance(
                        cryptoAttribute.getName());
                cipherMap.put(cryptoAttribute.getName(), cipher);
            } catch (NoSuchAlgorithmException e) {
                cipherMap.clear();
                throw new Exception(String.format(
                        "key generator maker not can initialized, because %s.",
                        cryptoAttribute.getName()));
            }
        }
        initialized = true;
    }

    public static byte[] encrypt(
            byte[] raw,
            Key secretKey,
            CryptoAttribute cryptoAttribute)
            throws Exception {
        initialize();
        Cipher cipher =
                cipherMap.get(
                        cryptoAttribute.getName());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(raw);
    }

    public static byte[] decrypt(
            byte[] decrypted,
            Key secretKey,
            CryptoAttribute cryptoAttribute)
            throws Exception {
        initialize();
        Cipher cipher =
                cipherMap.get(
                        cryptoAttribute.getName());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(decrypted);
    }
}
