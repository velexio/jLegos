package com.velexio.jlegos.crypto;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@NoArgsConstructor
public class Cryptor {

    //    private SecretKeySpec secretKey;
    private IvParameterSpec ivspec;
    private int AES_KEY_SIZE = 256;
    private int IV_SIZE = 96;
    private int TAG_BIT_LENGTH = 128;
    private String ALGO_TRANSFORMATION_STRING = "AES/GCM/PKCS5Padding";

    private byte[] tagData;
    private SecretKey secretKey;
    private byte iv[];
    private GCMParameterSpec gcmSpec;
    private SecureRandom secRand;

    @SneakyThrows
    public Cryptor(String encryptionKey) {
        setKey(encryptionKey);
    }

    public void setKey(String encryptionKey) throws NoSuchAlgorithmException {
        tagData = encryptionKey.getBytes(StandardCharsets.UTF_8);

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        secretKey = keyGen.generateKey();

        iv = new byte[IV_SIZE];
        secRand = new SecureRandom();
        secRand.nextBytes(iv);

        gcmSpec = new GCMParameterSpec(TAG_BIT_LENGTH, iv);

//        MessageDigest sha = MessageDigest.getInstance("SHA-512");
//        cryptKey = sha.digest(cryptKey);
//        cryptKey = Arrays.copyOf(cryptKey, 16);
//        secretKey = new SecretKeySpec(cryptKey, "AES");
//        Random rand = new Random();
//        byte[] ba = new byte[16];
//        rand.nextBytes(ba);
//        ivspec = new IvParameterSpec(ba);
//        SecureRandom secRan = new SecureRandom();
//        byte[] ranBytes = new byte[16];
//        secRan.nextBytes(ranBytes);
//        ivspec = new IvParameterSpec(ranBytes);
    }

    public String aesEncrypt(String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
        c.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec, new SecureRandom());
        c.updateAAD(tagData);
        byte[] cipherTextInByteArr = c.doFinal(message.getBytes());
        secRand.nextBytes(iv);
        return Base64.getEncoder().encodeToString(cipherTextInByteArr);
    }

    public String aesDecrypt(String encryptedMessage) throws BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException,
            InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException
    {
        byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        Cipher c = Cipher.getInstance(ALGO_TRANSFORMATION_STRING);
        c.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec, new SecureRandom());
//        c.updateAAD(tagData);

                return new String(c.doFinal(decodedMessage));

//        return plainTextInByteArr.toString();
    }

    public String encrypt(String rawString, String encryptionKey) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException,
            BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        setKey(encryptionKey);
        return encrypt(rawString);
    }

    public String encrypt(String rawString) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        checkSecretPresence();
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
        return Base64.getEncoder().encodeToString(cipher.doFinal(rawString.getBytes(StandardCharsets.UTF_8)));
    }

    public String decrypt(String encryptedValue) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException
    {
        checkSecretPresence();
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
        return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedValue)));
    }

    public String decrypt(String encryptedValue, String encryptionKey) throws NoSuchAlgorithmException, IllegalBlockSizeException,
            InvalidKeyException, BadPaddingException, NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        setKey(encryptionKey);
        return decrypt(encryptedValue);
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance("AES/CBC/PKCS5Padding");
    }

    private void checkSecretPresence() {
        if (StringUtils.isEmpty(secretKey)) {
            throw new RuntimeException("Must set the encryption key via 'setKey' or constructor method prior to running this operation");
        }
    }
}
