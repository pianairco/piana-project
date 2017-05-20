package ir.piana.dev.secure.random;

import org.junit.Test;

/**
 * @author Mohammad Rahmati, 4/15/2017 2:31 PM
 */
public class SecureRandomMakerTest {
    @Test
    public void test()
            throws Exception {
        byte[] bytes = SecureRandomMaker.makeByteArray(
                1024,
                SecureRandomType.SHA_1_PRNG);
        for(byte b : bytes) {
            System.out.println(b);
        }
    }

    @Test
    public void testSeed()
            throws Exception {
        byte[] bytes = SecureRandomMaker.makeByteArraySeed(
                10,
                SecureRandomType.SHA_1_PRNG);
        for(byte b : bytes) {
            System.out.println(b);
        }
    }
}
