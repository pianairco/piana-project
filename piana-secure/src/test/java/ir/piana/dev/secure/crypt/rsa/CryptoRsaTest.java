package ir.piana.dev.secure.crypt.rsa;

import ir.piana.dev.secure.crypto.CryptoAttribute;
import ir.piana.dev.secure.crypto.CryptoMaker;
import ir.piana.dev.secure.key.KeyPairAlgorithm;
import ir.piana.dev.secure.key.KeyPairMaker;
import ir.piana.dev.secure.util.HexConverter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.security.KeyPair;

/**
 * @author Mohammad Rahmati, 4/24/2017 4:50 PM
 */
public class CryptoRsaTest {
    private static KeyPair keyPair = null;
    static byte[] rawBytes;

    @BeforeClass
    public static void beforeClass()
            throws Exception {
        keyPair = KeyPairMaker
                .createKeyPair(
                        KeyPairAlgorithm.RSA_1024);
        rawBytes = new byte[] {9, 8, 7, 6, 5, 4, 3, 2, 1};
    }

    @Test
    public void encrypt()
            throws Exception {
        byte[] encrypted = CryptoMaker.encrypt(rawBytes,
                keyPair.getPublic(),
                CryptoAttribute.RSA);
        byte[] decrypted = CryptoMaker.decrypt(
                encrypted,
                keyPair.getPrivate(),
                CryptoAttribute.RSA);

        Assert.assertEquals(
                HexConverter.toHexString(rawBytes),
                HexConverter.toHexString(decrypted));
    }
}
