package com.velexio.jlegos.crypto;

import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class Cryptor {

    private SecretKeySpec secretKey;

    public void setKey(String encryptionKey) throws NoSuchAlgorithmException {
        byte[] cryptKey = encryptionKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-512");
        cryptKey = sha.digest(cryptKey);
        cryptKey = Arrays.copyOf(cryptKey, 16);
        secretKey = new SecretKeySpec(cryptKey, "AES");
    }

    public String enrypt(String rawString, String encryptionKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException
    {
        setKey(encryptionKey);
        return encrypt(rawString);
    }

    public String encrypt(String rawString) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException
    {
        checkSecretPresence();
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(rawString.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String encryptedValue) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException
    {
        checkSecretPresence();
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedValue)));
    }

    public String decrypt(String encryptedValue, String encryptionKey) throws NoSuchAlgorithmException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException
    {
        setKey(encryptionKey);
        return decrypt(encryptedValue);
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/ECP/PKCS5Padding");
    }

    private void checkSecretPresence() {
        if (StringUtils.isEmpty(secretKey)) {
            throw new RuntimeException("Must set the encyryption key via 'setKey' method prior to running this operation");
        }
    }
}
