package ir.piana.dev.secure.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * @author Mohammad Rahmati, 4/24/2017 3:35 PM
 */
public class HexConverter {
    public static String toHexString(byte[] raw) {
        return Hex.encodeHexString(raw);
    }

    public static byte[] fromHexString(String hex)
            throws DecoderException {
        return Hex.decodeHex(hex.toCharArray());
    }

    public static char[] toHexChars(byte[] raw) {
        return Hex.encodeHex(raw);
    }

    public static byte[] fromHexChars(char[] hex)
            throws DecoderException {
        return Hex.decodeHex(hex);
    }
}
