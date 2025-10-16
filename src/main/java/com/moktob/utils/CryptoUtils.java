package com.moktob.utils;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

@UtilityClass
@Slf4j
public class CryptoUtils {
    private static SecretKeySpec secretKey;

    private static void setKey(String secret) {
        try {
            byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hashedKey = sha256.digest(key);
            byte[] truncatedHashedKey = Arrays.copyOf(hashedKey, 16);
            secretKey = new SecretKeySpec(truncatedHashedKey, "AES");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String encrypt(final String strToEncrypt, final String secret) {
        try {
            setKey(secret);

            // Generate random IV
            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Use CBC mode instead of ECB
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));

            // Prepend IV to the encrypted data and encode
            byte[] combined = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            log.error("Error while encrypting: ", ex);
        }
        return null;
    }

    public static String decrypt(final String strToDecrypt, final String secret) {
        try {
            setKey(secret);

            // Decode from Base64
            byte[] combined = Base64.getDecoder().decode(strToDecrypt);

            // Extract IV from the beginning of the data
            byte[] iv = new byte[16];
            byte[] encrypted = new byte[combined.length - 16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, encrypted, 0, encrypted.length);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Use CBC mode for decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Error while decrypting: ", ex);
            log.error("Secret Key: ", secretKey);
        }
        return null;
    }
}
