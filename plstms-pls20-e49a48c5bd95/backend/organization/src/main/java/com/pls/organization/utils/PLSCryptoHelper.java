package com.pls.organization.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class which provide static methods for cipher/decipher string values.
 * 
 * @author Dmitriy Davydenko
 *
 */
public final class PLSCryptoHelper {

    private static final String ALGORITHM = "DES";
    private static final String HEX = "0123456789ABCDEF";

    /**
     * Don't let anyone instantiate this class.
     */
    private PLSCryptoHelper() {

    };
    /**
     * Encrypt data.
     * @param secretKey -   a secret key used for encryption.
     * @param data      -   data to encrypt.
     * @return  Encrypted data.
     * @throws Exception - any java exception.
     */
    public static String encrypt(String secretKey, String data) throws Exception {
        // Key has to be of length 8
        if (secretKey == null || secretKey.length() != 8) {
            throw new IllegalArgumentException("Invalid key length - 8 bytes key needed!");
        }

        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return toHex(cipher.doFinal(data.getBytes()));
    }

    /**
     * Decrypt data.
     * @param secretKey -   a secret key used for decryption.
     * @param data      -   data to decrypt.
     * @return  Decrypted data.
     * @throws Exception - any java exception.
     */
    public static String decrypt(String secretKey, String data) throws Exception {
        // Key has to be of length 8
        if (secretKey == null || secretKey.length() != 8) {
            throw new IllegalArgumentException("Invalid key length - 8 bytes key needed!");
        }

        SecretKey key = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return new String(cipher.doFinal(toByte(data)));
    }

    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }

        return result;
    }

    /**
     * Converts array of bites to HEX representation of the encrypted data.
     * 
     * @param stringBytes - encrypted data.
     * @return HEX representation of encrypted data.
     */
    private static String toHex(byte[] stringBytes) {
        StringBuffer result = new StringBuffer(2 * stringBytes.length);

        for (int i = 0; i < stringBytes.length; i++) {
            result.append(HEX.charAt((stringBytes[i] >> 4) & 0x0f)).append(HEX.charAt(stringBytes[i] & 0x0f));
        }

        return result.toString();
    }
}
