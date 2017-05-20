package ir.piana.dev.secure.util;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Mohammad Rahmati, 4/24/2017 3:40 PM
 */
public class Base64ConverterTest {
    static byte[] RAW_BYTES;

    @BeforeClass
    public static void beforeClass() {
        RAW_BYTES = new byte[] {9, 8, 7, 6, 5, 4, 3, 2, 1};
    }

    @Test
    public void testBase64Array() {
        byte[] base64 = Base64Converter.toBase64Bytes(RAW_BYTES);
        for(byte b : base64)
            System.out.print(b);
        System.out.println("");
        byte[] rawBytes = Base64Converter.fromBase64Bytes(base64);
        for(byte b : rawBytes)
            System.out.print(b);
    }

    @Test
    public void testBase64String() {
        String base64 = Base64Converter.toBase64String(RAW_BYTES);
        System.out.println(base64);
        byte[] rawBytes = Base64Converter.fromBase64String(base64);
        for(byte b : rawBytes)
            System.out.print(b);
    }
}
