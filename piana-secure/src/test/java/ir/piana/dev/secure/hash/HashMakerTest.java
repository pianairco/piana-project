package ir.piana.dev.secure.hash;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Mohammad Rahmati, 4/15/2017 10:35 AM
 */
public class HashMakerTest {
    @Test
    public void testHexHash()
            throws Exception {
        String hexHash = HashMaker.getHexHash(
                "test", HashType.SHA_512);
        Assert.assertEquals("hex hash not corrected.",
                "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67b143732c304cc5fa9ad8e6f57f50028a8ff",
                hexHash);
    }

    @Test
    public void testBase64Hash()
            throws Exception {
        String base64Hash = HashMaker.getBase64Hash(
                "test", HashType.SHA_512);
        Assert.assertEquals("base64 hash not corrected.",
                "7iaw3Ur350mqGo7jwQrpkj9hiYB3Lkc/iBml1JQODbJ6wYX4oOHV+E+IvIh/1nsUNzLDBMxfqa2Ob1f1ACio/w==",
                base64Hash);
    }
}
