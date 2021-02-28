package com.velexio.jlegos.crypto;

import com.velexio.jlegos.util.FileCopyOption;
import com.velexio.jlegos.util.FileUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CryptorTest {

    private static String staticPass1 = "static#pass";
    private static String staticText1 = "Whenever you find yourself on the side of the majority, it is time to pause and reflect. Mark Twain";
    private static String storedEncryptedValue;
    private String testPass1 = "foobarkey";
    private String testText1 = "There is a better way and a better way yet";
    private String testText2 = "You miss 100% of the shots you don't take.";

    @BeforeAll
    static void setStoredEncryptedValue() {
        Cryptor cryptor = new Cryptor();
        storedEncryptedValue = cryptor.encrypt(CryptorTest.staticText1, CryptorTest.staticPass1);
    }

    @BeforeEach
    void prepEncryptFiles() throws IOException {
        String simpleOrigPath = "src/test/resources/FileEncryption/simple-orig.txt";
        String simpleTestPath = "src/test/resources/FileEncryption/simple.txt";
        File simpleOrigFile = new File(simpleOrigPath);
        File simpleTestFile = new File(simpleTestPath);
        FileUtils.copyFile(simpleOrigFile.getAbsolutePath(), simpleTestFile.getAbsolutePath(), FileCopyOption.REPLACE_EXISTING);
    }


    @Test
    @SneakyThrows
    void basicTest() {
        Cryptor cryptor = new Cryptor();
        String enryptedText = cryptor.encrypt(testText1, testPass1);
        String decyrptedText = cryptor.decrypt(enryptedText, testPass1);
        assertEquals(testText1, decyrptedText);

    }

    @Test
    @SneakyThrows
    void decryptingPreviousStoredWorks() {
        Cryptor cryptor = new Cryptor();
        String decyrptedText = cryptor.decrypt(storedEncryptedValue, CryptorTest.staticPass1);
        assertEquals(CryptorTest.staticText1, decyrptedText);
    }

    @SneakyThrows
    @Test
    void enryptFileWorks() throws IOException, URISyntaxException {
        Cryptor cryptor = new Cryptor();
        String origFilePath = "src/test/resources/FileEncryption/simple-orig.txt";
        File origFile = new File(origFilePath);
        String filePath = "src/test/resources/FileEncryption/simple.txt";
        File file = new File(filePath);
        String origContents = Files.readString(Paths.get(file.getAbsolutePath()));
        cryptor.encryptFile(file.getAbsolutePath(), testPass1);
        String encryptedContents = Files.readString(Paths.get(file.getAbsolutePath()));
        assertNotEquals(origContents, encryptedContents, "The file encryption did not seem to work");
        cryptor.decryptFile(file.getAbsolutePath(), testPass1);
        String newContents = Files.readString(Paths.get(file.getAbsolutePath()));
        assertEquals(origContents, newContents, "The file contents changed during encrypt/decrypt");
    }


}
