package ir.piana.dev.secure.random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mohammad Rahmati, 4/15/2017 2:45 PM
 */
public class SecureRandomMaker {
    private static final
    Map<String, SecureRandom> secureRandomMap
            = new HashMap<>();
    private static boolean initialized = false;

    private static synchronized void initialize()
            throws Exception {
        if(initialized)
            return;
        for (SecureRandomType value : SecureRandomType.values()) {
            try {
                secureRandomMap.put(value.getName(),
                        SecureRandom.getInstance(value.getName())
                );
            } catch (NoSuchAlgorithmException e) {
                secureRandomMap.clear();
                throw new Exception(String.format(
                        "hash maker not can initialized, because %s.",
                        value.getName()));
            }
        }
        initialized = true;
    }

    public static byte[] makeByteArray(
            int randomByteLength, SecureRandomType secureRandomType)
            throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(secureRandomType.getName());
        byte[] bytes = new byte[randomByteLength];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static byte[] makeByteArraySeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        byte[] bytes = new byte[seedLength];
        secureRandom.nextBytes(bytes);
        return bytes;
    }

    public static int makeInt(
            SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        return secureRandom.nextInt();
    }

    public static int makeIntSeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        return secureRandom.nextInt();
    }

    public static long makeLong(
            SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        return secureRandom.nextLong();
    }

    public static long makeLongSeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        return secureRandom.nextLong();
    }

    public static boolean makeBoolean(
            SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        return secureRandom.nextBoolean();
    }

    public static boolean makeBooleanSeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        return secureRandom.nextBoolean();
    }

    public static double makeDouble(
            SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        return secureRandom.nextDouble();
    }

    public static double makeDoubleSeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        return secureRandom.nextDouble();
    }

    public static float makefloat(
            SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        return secureRandom.nextFloat();
    }

    public static float makeFloatSeed(
            int seedLength, SecureRandomType secureRandomType
    ) throws Exception {
        initialize();
        SecureRandom secureRandom =
                secureRandomMap.get(
                        secureRandomType.getName());
        byte[] seed =
                secureRandom.generateSeed(seedLength);
        secureRandom.setSeed(seed);
        return secureRandom.nextFloat();
    }
}
