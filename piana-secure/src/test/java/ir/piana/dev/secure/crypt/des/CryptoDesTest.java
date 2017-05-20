package ir.piana.dev.secure.crypt.des;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

/**
 * @author Mohammad Rahmati, 4/16/2017 1:50 PM
 */
public class CryptoDesTest {
    @Test
    public void test()
            throws DecoderException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            InvalidKeySpecException {
        byte[] theKey =
                Hex.decodeHex("133457799BBCDFF1".toCharArray());
        KeySpec ks = new DESKeySpec(theKey);
        SecretKeyFactory kf
                = SecretKeyFactory.getInstance("DES");
        SecretKey ky = kf.generateSecret(ks);
        SecretKey s = new SecretKeySpec(theKey, "DES");

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56);
        SecretKey skey = kg.generateKey();
    }
}
