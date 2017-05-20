package ir.piana.dev.secure.hash;

import ir.piana.dev.secure.util.Base64Converter;
import ir.piana.dev.secure.util.HexConverter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 4/15/2017 10:12 AM
 */
public abstract class HashMaker {
    private static final
    Map<String, MessageDigest> messageDigestMap
            = new HashMap<>();
    private static boolean initialized = false;

    private static synchronized void initialize()
            throws Exception {
        if(initialized)
            return;
        for (HashType value : HashType.values()) {
            try {
                messageDigestMap.put(value.getName(),
                        MessageDigest.getInstance(value.getName())
                );
            } catch (NoSuchAlgorithmException e) {
                messageDigestMap.clear();
                throw new Exception(String.format(
                        "hash maker not can initialized, because %s.",
                        value.getName()));
            }
        }
        initialized = true;
    }

    public static byte[] makeHash(
            byte[] rawBytes, HashType hashType)
            throws Exception {
        initialize();
        MessageDigest messageDigest =
                messageDigestMap.get(hashType.getName());
        messageDigest.update(rawBytes);
        return messageDigest.digest();
    }

    public static byte[] makeHash(
            String message, HashType hashType)
            throws Exception {
        return makeHash(message.getBytes(), hashType);
    }

    public static String getHexHash(
            String message, HashType hashType)
            throws Exception {
        return HexConverter.toHexString(
                makeHash(message, hashType));
    }

    public static String getBase64Hash(
            String message, HashType hashType)
            throws Exception {
        return Base64Converter.toBase64String(
                makeHash(message, hashType));
    }
}
