package org.example.age.data.crypto;

import java.security.SecureRandom;

/** Utilities for random numbers generated using a cryptographically strong random number generator. */
final class SecureRandomUtils {

    private static final SecureRandom random = new SecureRandom();

    /** Generates the specified number of bytes using a cryptographically strong random number generator. */
    public static byte[] generateBytes(int length) {
        byte[] bytes = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    // static class
    private SecureRandomUtils() {}
}
