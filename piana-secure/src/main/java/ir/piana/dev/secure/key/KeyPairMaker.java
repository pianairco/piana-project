package ir.piana.dev.secure.key;

import ir.piana.dev.secure.PianaSecureException;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 4/16/2017 6:24 PM
 */
public class KeyPairMaker {
    private static boolean initialized = false;
    private static final
    Map<String, KeyPairGenerator> keyPairGenMap
            = new HashMap<>();

    private static synchronized void initialize()
            throws PianaSecureException {
        if (initialized)
            return;
        for (KeyPairAlgorithm keyPairAlgorithm
                : KeyPairAlgorithm.values()) {
            try {
                KeyPairGenerator keyPairGenerator =
                        KeyPairGenerator.getInstance(
                                keyPairAlgorithm.getJceName());
                keyPairGenerator.initialize(
                        keyPairAlgorithm.getKeySize());
                keyPairGenMap.put(
                        keyPairAlgorithm.getName(),
                        keyPairGenerator);
            } catch (NoSuchAlgorithmException e) {
                keyPairGenMap.clear();
                throw new PianaSecureException(String.format(
                        "key generator maker not can initialized, because %s.",
                        keyPairAlgorithm.getName()));
            }
        }
        initialized = true;
    }

    public static KeyPair createKeyPair(
            KeyPairAlgorithm keyPairAlgorithm)
            throws PianaSecureException {
        initialize();
        KeyPairGenerator keyPairGenerator =
                keyPairGenMap.get(
                        keyPairAlgorithm.getName());
        return keyPairGenerator.generateKeyPair();
    }

//    public static byte[] encrypt(
//            byte[] raw,
//            SecretKey secretKey,
//            CryptoAttribute cryptoAttribute)
//            throws Exception {
//        initialize();
//        Cipher cipher =
//                keyPairGenMap.get(
//                        cryptoAttribute.getName());
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        return cipher.doFinal(raw);
//    }
//
//    public static byte[] decrypt(
//            byte[] decrypted,
//            SecretKey secretKey,
//            CryptoAttribute cryptoAttribute)
//            throws Exception {
//        initialize();
//        Cipher cipher =
//                keyPairGenMap.get(
//                        cryptoAttribute.getName());
//        cipher.init(Cipher.DECRYPT_MODE, secretKey);
//        return cipher.doFinal(decrypted);
//    }
}
