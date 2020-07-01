package com.velexio.jlegos.crypto;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CryptorTest {

    private String testKey = "foobarkey";
    private String testText = "There is a better way and a better way yet";

    @Test
    void enryptWithKeySetWorks() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException
    {
        Cryptor cryptor = new Cryptor(testKey);
        String enryptedText = cryptor.aesEncrypt(testText);
        assertEquals(80, enryptedText.length());
        String decyrptedText = cryptor.aesDecrypt(enryptedText);
        assertEquals(testText, decyrptedText);

        Cryptor cryptor2 = new Cryptor(testKey);
        String decryptText2 = cryptor2.aesDecrypt(enryptedText);
        assertEquals(testText, decryptText2);
//        String encryptedText = cryptor.encrypt(testText);
//        assertEquals(64, encryptedText.length());
//        String decryptedText = cryptor.decrypt(encryptedText);
//        assertEquals(testText, decryptedText);
//
//        Cryptor cryptor2 = new Cryptor(testKey);
//        String decryptTextNew = cryptor2.decrypt(encryptedText);
//        assertEquals(testText, decryptTextNew);
    }

    @Test
    void encrypt() {
    }

    @Test
    void decrypt() {
    }

    @Test
    void testDecrypt() {
    }
}