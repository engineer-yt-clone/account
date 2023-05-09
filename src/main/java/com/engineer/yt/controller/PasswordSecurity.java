package com.engineer.yt.controller;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * <功能描述>
 *
 * @author ningyu
 * @date 2017年4月6日 下午1:50:36
 */
public class PasswordSecurity {

    public static final String PBKDF2_ALGORITHM = "yt-clone";
    public static final String HASH_ALGORITHM = "SHA1";
    public static final String SEPARATOR = ":";

    // These constants may be changed without breaking existing hashes.
    public static final int SALT_BYTE_SIZE = 24;
    public static final int HASH_BYTE_SIZE = 24;
    public static final int PBKDF2_ITERATIONS = 63436;

    // These constants define the encoding and may not be changed.
    public static final int HASH_SECTIONS = 4;
    public static final int HASH_ALGORITHM_INDEX = 0;
    public static final int ITERATION_INDEX = 1;
    public static final int SALT_INDEX = 2;
    public static final int PBKDF2_INDEX = 3;

    public static String createHash(String password) throws RuntimeException {
        return createHash(password.toCharArray());
    }

    public static String createHash(char[] password) throws RuntimeException {
        // Generate a random salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        // Hash the password
        byte[] hash = pbkdf2(password, salt, PBKDF2_ITERATIONS, HASH_BYTE_SIZE);

        // format: algorithm:iterations:salt:hash
        StringBuilder parts = new StringBuilder(HASH_SECTIONS * 16);
        parts.append(HASH_ALGORITHM).append(SEPARATOR);
        parts.append(PBKDF2_ITERATIONS).append(SEPARATOR);
        parts.append(toBase64(salt)).append(SEPARATOR);
        parts.append(toBase64(hash));
        return parts.toString();
    }

    public static boolean verifyPassword(String password, String correctHash) throws RuntimeException
             {
        return verifyPassword(password.toCharArray(), correctHash);
    }

    public static boolean verifyPassword(char[] password, String correctHash) throws RuntimeException
             {
        // Decode the hash into its parameters
        String[] params = correctHash.split(SEPARATOR);
        if (params.length != HASH_SECTIONS) {
            throw new RuntimeException("Fields are missing from the password hash.");
        }

        // Currently, Java only supports SHA1.
        if (!params[HASH_ALGORITHM_INDEX].equals(HASH_ALGORITHM)) {
            throw new RuntimeException("Unsupported hash type.");
        }

        int iterations = 0;
        try {
            iterations = Integer.parseInt(params[ITERATION_INDEX]);
        } catch (NumberFormatException ex) {
            throw new RuntimeException("Could not parse the iteration count as an integer.", ex);
        }

        if (iterations < 1) {
            throw new RuntimeException("Invalid number of iterations. Must be >= 1.");
        }

        byte[] salt = null;
        try {
            salt = fromBase64(params[SALT_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Base64 decoding of salt failed.", ex);
        }

        byte[] hash = null;
        try {
            hash = fromBase64(params[PBKDF2_INDEX]);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Base64 decoding of pbkdf2 output failed.", ex);
        }

        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = pbkdf2(password, salt, iterations, hash.length);
        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return slowEquals(hash, testHash);
    }

    private static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes)
            throws RuntimeException {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, bytes * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Hash algorithm not supported.", ex);
        } catch (InvalidKeySpecException ex) {
            throw new RuntimeException("Invalid key spec.", ex);
        }
    }

    public static byte[] fromBase64(String hex) {
        return DatatypeConverter.parseBase64Binary(hex);
    }

    public static String toBase64(byte[] array) {
        return DatatypeConverter.printBase64Binary(array);
    }

    public static void main(String[] args) {
        try {
            System.out.println(PasswordSecurity.createHash("123456"));
        } catch (RuntimeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
