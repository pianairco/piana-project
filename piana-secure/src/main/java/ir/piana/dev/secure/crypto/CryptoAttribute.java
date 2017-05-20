package ir.piana.dev.secure.crypto;

/**
 * @author Mohammad Rahmati, 4/16/2017 6:25 PM
 */
public enum CryptoAttribute {
    DES_ECB_PKCS_5_PADDING("DES/ECB/PKCS5PADDING",
            CryptoAlgorithm.DES,
            CryptoMode.ECB,
            CryptoPadding.PKCS_5_PADDING),
    DES_CBC_PKCS_5_PADDING("DES/CBC/PKCS5PADDING",
            CryptoAlgorithm.DES,
            CryptoMode.CBC,
            CryptoPadding.PKCS_5_PADDING),
    DES_CFB_PKCS_5_PADDING("DES/CFB/PKCS5PADDING",
            CryptoAlgorithm.DES,
            CryptoMode.CFB,
            CryptoPadding.PKCS_5_PADDING),
    DES_OFB_PKCS_5_PADDING("DES/OFB/PKCS5PADDING",
            CryptoAlgorithm.DES,
            CryptoMode.OFB,
            CryptoPadding.PKCS_5_PADDING),
    RSA("RSA",
            CryptoAlgorithm.RSA,
            null,
            null);

    private String name;
    private CryptoAlgorithm jceAlgorithm;
    private CryptoMode jceMode;
    private CryptoPadding jcePadding;

    private CryptoAttribute(
            String name,
            CryptoAlgorithm jceAlgorithm,
            CryptoMode jceMode,
            CryptoPadding jcePadding) {
        this.name = name;
        this.jceAlgorithm = jceAlgorithm;
        this.jceMode = jceMode;
        this.jcePadding = jcePadding;
    }

    public String getName() {
        return name;
    }

    public CryptoAlgorithm getJceAlgorithm() {
        return jceAlgorithm;
    }

    public CryptoMode getJceMode() {
        return jceMode;
    }

    public CryptoPadding getJcePadding() {
        return jcePadding;
    }
}
