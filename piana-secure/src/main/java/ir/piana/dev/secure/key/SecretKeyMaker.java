package ir.piana.dev.secure.key;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static javax.crypto.KeyGenerator.*;


/**
 * @author Mohammad Rahmati, 4/16/2017 4:10 PM
 */
public abstract class SecretKeyMaker {
    private static boolean initialized = false;
    private static final
    Map<String, KeyGenerator> keyGeneratorMap
            = new HashMap<>();
    private static final
    Map<String, SecretKeyFactory> keyFactoryMap
            = new HashMap<>();

    private static synchronized void initialize()
            throws Exception {
        if (initialized)
            return;
        for (SecretKeyAlgorithm value
                : SecretKeyAlgorithm.values()) {
            try {
                KeyGenerator keyGenerator = getInstance(
                        value.getJceName());
                keyGenerator.init(value.getKeySize());
                keyGeneratorMap.put(value.getName(),
                        keyGenerator);
                if(!value.getJceName().equalsIgnoreCase(
                        SecretKeyAlgorithm.AES_128.getJceName()))
                    keyFactoryMap.put(value.getName(),
                            SecretKeyFactory.getInstance(
                                    value.getJceName()));
            } catch (NoSuchAlgorithmException e) {
                keyGeneratorMap.clear();
                keyFactoryMap.clear();
                throw new Exception(String.format(
                        "key generator maker not can initialized, because %s.",
                        value.getName()));
            }
        }
        initialized = true;
    }

    public static SecretKey make(
            byte[] key,
            SecretKeyAlgorithm secretKeyAlgorithm)
            throws Exception {
                initialize();
        if(secretKeyAlgorithm.getJceName()
                .equalsIgnoreCase(
                        SecretKeyAlgorithm.AES_128.getJceName()))
            return makeSecretKeySpec(key, secretKeyAlgorithm);
        KeySpec ks = null;
        try {
            ks = new DESKeySpec(key);
            SecretKeyFactory kf = keyFactoryMap.get(
                    secretKeyAlgorithm.getName());
            return kf.generateSecret(ks);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public static SecretKey makeSecretKeySpec(
            byte[] key,
            SecretKeyAlgorithm secretKeyAlgorithm)
            throws Exception {
        initialize();
        return new SecretKeySpec(
                key, secretKeyAlgorithm.getJceName());
    }

    public static SecretKey make(
            SecretKeyAlgorithm secretKeyAlgorithm)
            throws Exception {
        initialize();
        KeyGenerator kg = keyGeneratorMap.get(
                secretKeyAlgorithm.getName());
        return kg.generateKey();
    }

    public static String asString(
            SecretKey secretKey)
            throws Exception {
        initialize();
        return Base64.getEncoder()
                .encodeToString(
                        secretKey.getEncoded());
    }

    public static SecretKey asSecretKey(
            String encodedKey,
            SecretKeyAlgorithm secretKeyAlgorithm)
            throws Exception {
                initialize();
        byte[] decodedKey = Base64.getDecoder()
                .decode(encodedKey);
        return make(decodedKey, secretKeyAlgorithm);
    }
}
