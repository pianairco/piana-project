package ir.piana.dev.secure.util;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author Mohammad Rahmati, 4/24/2017 4:10 PM
 */
public class ByteStringConverter {
    public static byte[] toBytes(
            String string,
            Charset charset)
            throws UnsupportedEncodingException {
        return string.getBytes(charset);
    }

    public static String fromBytes(
            byte[] bytes,
            Charset charset)
            throws UnsupportedEncodingException {
        return new String(bytes, charset);
    }

    public static byte[] toUtf8Bytes(
            String string)
            throws UnsupportedEncodingException {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String fromUtf8Bytes(
            byte[] bytes)
            throws UnsupportedEncodingException {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] toAsciiBytes(
            String string)
            throws UnsupportedEncodingException {
        return string.getBytes(StandardCharsets.US_ASCII);
    }

    public static String fromAsciiBytes(
            byte[] bytes)
            throws UnsupportedEncodingException {
        return new String(bytes, StandardCharsets.US_ASCII);
    }

    public static byte[] toIso8859Bytes(
            String string)
            throws UnsupportedEncodingException {
        return string.getBytes(StandardCharsets.ISO_8859_1);
    }

    public static String fromIso8859Bytes(
            byte[] bytes)
            throws UnsupportedEncodingException {
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }
}
