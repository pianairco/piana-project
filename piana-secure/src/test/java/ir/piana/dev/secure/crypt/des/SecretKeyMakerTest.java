package ir.piana.dev.secure.crypt.des;

import ir.piana.dev.secure.key.SecretKeyAlgorithm;
import ir.piana.dev.secure.key.SecretKeyMaker;
import org.junit.Test;

import javax.crypto.SecretKey;

/**
 * @author Mohammad Rahmati, 4/16/2017 5:00 PM
 */
public class SecretKeyMakerTest {
    @Test
    public void test()
            throws Exception {
        SecretKey secretKey =
                SecretKeyMaker.make(
                        SecretKeyAlgorithm.AES_192);
        String keyString =
                SecretKeyMaker.asString(secretKey);
        System.out.println(keyString);
        SecretKey secretKey1 = SecretKeyMaker.asSecretKey(keyString,
                SecretKeyAlgorithm.AES_192);
    }
}
