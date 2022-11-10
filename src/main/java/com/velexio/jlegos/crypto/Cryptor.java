package com.velexio.jlegos.crypto;

import com.velexio.jlegos.util.FileUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;


@Getter
@Slf4j
public class Cryptor {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final String ENCRYPTION_ALGO = "AES/GCM/NoPadding";
    private static final String KEY_ALGO = "PBKDF2WithHmacSHA256";
    private static final int KEY_SIZE = 256;
    private static final int KEY_GEN_ITERATIONS = 128455;
    private static final int TAG_BIT_LENGTH = 128;
    private static final int IV_BYTE_LENGTH = 24;
    private static final int SALT_BYTE_LENGTH = 16;

    @SneakyThrows
    private static SecretKey getAESKey() {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(KEY_SIZE, SecureRandom.getInstanceStrong());
        return keyGenerator.generateKey();
    }

    @SneakyThrows
    private static SecretKey getAESKey(String password, byte[] keySalt) throws InvalidKeySpecException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGO);
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), keySalt, KEY_GEN_ITERATIONS, KEY_SIZE);
        return new SecretKeySpec(keyFactory.generateSecret(keySpec).getEncoded(), "AES");
    }

    @SneakyThrows
    public String encrypt(String message, String encryptionPassword) {

        byte[] initVector = getRandomBytes(IV_BYTE_LENGTH);
        byte[] encryptSalt = getRandomBytes(SALT_BYTE_LENGTH);
        Cipher c = getCipher();
        SecretKey secretKey = Cryptor.getAESKey(encryptionPassword, encryptSalt);
        c.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BIT_LENGTH, initVector));
        byte[] encryptedBytes = c.doFinal(message.getBytes(UTF_8));
        byte[] saltedBytes = ByteBuffer.allocate(initVector.length + encryptSalt.length + encryptedBytes.length)
                .put(initVector)
                .put(encryptSalt)
                .put(encryptedBytes)
                .array();
        return Base64.getEncoder().encodeToString(saltedBytes);
    }

    /**
     * Will encrypt a plain text file. Keep in mind this is not designed for large files, rather text files that are
     * relatively small (in the MB range).
     *
     * @param filePath           The full path to the file
     * @param encryptionPassword The password to be used for encryption
     * @throws URISyntaxException
     * @throws IOException
     */
    @SneakyThrows
    public void encryptFile(String filePath, String encryptionPassword) throws URISyntaxException, IOException {
        byte[] initVector = getRandomBytes(IV_BYTE_LENGTH);
        byte[] encryptSalt = getRandomBytes(SALT_BYTE_LENGTH);
        SecretKey secretKey = getAESKey(encryptionPassword, encryptSalt);
        Cipher cipher = getCipher();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BIT_LENGTH, initVector));
        String fileContent = Files.readString(Paths.get(filePath), UTF_8);
        String fileContentEncrypted = encrypt(fileContent, encryptionPassword);
        FileUtils.rename(filePath, filePath + ".raw");
        try {
            FileUtils.touch(filePath);
            FileUtils.append(filePath, fileContentEncrypted, false);
            FileUtils.delete(filePath + ".raw");
        } catch (IOException ioe) {
            FileUtils.rename(filePath + ".raw", filePath);
            log.error("IO exception occurred during file encryption...file reverted to original", ioe);
            throw ioe;
        }
    }

    /**
     * Will decrypt the previously encrypted value, given the same encryption key/password
     *
     * @param encryptedValue
     * @param encryptionPassword
     * @return
     * @throws InvalidKeyException
     */
    @SneakyThrows
    public String decrypt(String encryptedValue, String encryptionPassword) throws InvalidKeyException {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue.getBytes(UTF_8));
        ByteBuffer encryptedSet = ByteBuffer.wrap(decodedBytes);
        byte[] initVector = new byte[IV_BYTE_LENGTH];
        encryptedSet.get(initVector);
        byte[] encryptSalt = new byte[SALT_BYTE_LENGTH];
        encryptedSet.get(encryptSalt);
        byte[] encryptedBytes = new byte[encryptedSet.remaining()];
        encryptedSet.get(encryptedBytes);

        SecretKey secretKey = Cryptor.getAESKey(encryptionPassword, encryptSalt);
        Cipher cipher = getCipher();
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(TAG_BIT_LENGTH, initVector));
        return new String(cipher.doFinal(encryptedBytes), UTF_8);
    }

    /**
     * Will decrypt a previously encrypted file.
     *
     * @param filePath
     * @param encryptionPassword
     * @throws IOException
     * @throws InvalidKeyException
     */
    public void decryptFile(String filePath, String encryptionPassword) throws IOException, InvalidKeyException {
        String fileContent = Files.readString(Paths.get(filePath), UTF_8);
        String decryptedContent = decrypt(fileContent, encryptionPassword);
        FileUtils.rename(filePath, filePath + ".enc");
        try {
            FileUtils.touch(filePath);
            FileUtils.append(filePath, decryptedContent, false);
            FileUtils.delete(filePath + ".enc");
        } catch (IOException ioe) {
            FileUtils.rename(filePath + ".enc", filePath);
            log.error("IO exception occurred during file decryption. File reverted to original", ioe);
            throw ioe;
        }
    }

    private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        return Cipher.getInstance(ENCRYPTION_ALGO);
    }

    /**
     * Will generate a random byte[] that can be used as a initialization vector for encrypt/decrypt functions
     *
     * @param size The size of IV to generate.  Default is
     * @return
     */
    private byte[] getRandomBytes(int size) {
        byte[] iv = new byte[size];
        new SecureRandom().nextBytes(iv);
        return iv;
    }


}
