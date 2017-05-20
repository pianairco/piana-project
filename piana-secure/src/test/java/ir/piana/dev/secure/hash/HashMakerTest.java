package ir.piana.dev.secure.hash;

import org.junit.Test;

/**
 * @author Mohammad Rahmati, 4/15/2017 10:35 AM
 */
public class HashMakerTest {
    @Test
    public void test() throws Exception {
        System.out.println(
                HashMaker.getHexHash(
                        "test", HashType.SHA_512));
        System.out.println(
                HashMaker.getBase64Hash(
                        "test", HashType.SHA_512));
    }
}
