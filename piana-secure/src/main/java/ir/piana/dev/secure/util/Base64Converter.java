package ir.piana.dev.secure.util;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Mohammad Rahmati, 4/24/2017 3:38 PM
 */
public class Base64Converter {
    public static String toBase64String(byte[] row) {
        return Base64.encodeBase64String(row);
    }

    public static byte[] fromBase64String(String base64) {
        return Base64.decodeBase64(base64);
    }

    public static byte[] toBase64Bytes(byte[] row) {
        return Base64.encodeBase64(row);
    }

    public static byte[] fromBase64Bytes(byte[] base64) {
        return Base64.decodeBase64(base64);
    }
}
