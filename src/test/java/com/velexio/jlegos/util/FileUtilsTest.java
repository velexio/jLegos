package com.velexio.jlegos.util;

import com.velexio.jlegos.exceptions.ChecksumGenerationException;
import com.velexio.jlegos.exceptions.EnsureDirectoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    private File testStageDir = new File(FileUtils.getTempDir() + "/file-utils-test-stage");
    private final String BLOCK_FILE_PATH = testStageDir.getAbsolutePath() + "/block-file";
    private final String EXIST_DIR_PATH = testStageDir.getAbsolutePath() + "/existing-dir";
    private final String COPY_SOURCE_FILE_1 = testStageDir.getAbsolutePath() + "/source.txt";
    private final String COPY_DEST_FILE_1 = testStageDir.getAbsolutePath() + "/dest.txt";

    @BeforeEach
    void setupEach() throws IOException {
        File existDir = new File(EXIST_DIR_PATH);
        if (testStageDir.mkdirs() && existDir.mkdir()) {
            FileUtils.touch(BLOCK_FILE_PATH);
            FileUtils.append(COPY_SOURCE_FILE_1, "source", false);
        } else {
            fail("Unable to create test dir -> " + testStageDir.getAbsolutePath());
        }
        System.out.println("Test stage dir is -> " + testStageDir.getAbsolutePath());
    }

    @AfterEach
    void tearDownEach() throws IOException {
        FileUtils.deleteDirectory(testStageDir.getAbsolutePath());
    }

    @Test
    void changeCopyBufferSize() {
        FileUtils.changeCopyBufferSize(8192);
        assertEquals(8192, FileUtils.getCopyBufferSize());
        FileUtils.changeCopyBufferSize(1);
        assertEquals(4, FileUtils.getCopyBufferSize());
        FileUtils.changeCopyBufferSize(100000000);
        assertEquals(8388608, FileUtils.getCopyBufferSize());
    }

    @Test
    void checkIsHiddenWorks() throws IOException {
        String hiddenFilePath = testStageDir + "/.hiddenFile";
        String hiddenDirPath = testStageDir + "/.hiddenDir";
        String regFilePath = testStageDir + "/regFile.txt";
        String regDirPath = testStageDir + "/regDir";
        String nonExistFilePath = testStageDir + "/nonExistFile";
        FileUtils.touch(hiddenFilePath);
        FileUtils.touch(regFilePath);
        FileUtils.createDirectory(hiddenDirPath);
        FileUtils.createDirectory(regDirPath);

        assertTrue(FileUtils.isHidden(hiddenFilePath));
        assertTrue(FileUtils.isHidden(hiddenDirPath), "Test against hidden directory");
        assertFalse(FileUtils.isHidden(regFilePath));
        assertFalse(FileUtils.isHidden(regDirPath), "test against regular directory");
        assertFalse(FileUtils.isHidden(nonExistFilePath));
    }

    @Test
    void checkIsFileWorks() throws IOException {
        String hiddenFilePath = testStageDir + "/.hiddenFile";
        String regFilePath = testStageDir + "/regFile.txt";
        String nonExistFilePath = testStageDir + "/nonExistFile";
        String hiddenDirPath = testStageDir + "/.hiddenDir";
        String regDirPath = testStageDir + "/regDir";
        FileUtils.touch(hiddenFilePath);
        FileUtils.touch(regFilePath);
        FileUtils.createDirectory(hiddenDirPath);
        FileUtils.createDirectory(regDirPath);

        assertTrue(FileUtils.isFile(hiddenFilePath), "Test against a hidden file");
        assertTrue(FileUtils.isFile(regFilePath));
        assertFalse(FileUtils.isFile(nonExistFilePath));
        assertFalse(FileUtils.isFile(hiddenDirPath), "Test against a hiddend directory");
        assertFalse(FileUtils.isFile(regDirPath), "Test against regular directory");
    }

    @Test
    void checkIsDirWorks() throws IOException {
        String hiddenDirPath = testStageDir + "/.hiddenDir";
        String regDirPath = testStageDir + "/regDir";
        String nonExistDirPath = testStageDir + "/nonExistDir";
        FileUtils.createDirectory(hiddenDirPath);
        FileUtils.createDirectory(regDirPath);
        assertTrue(FileUtils.isDir(hiddenDirPath), "Test against hidden directory");
        assertTrue(FileUtils.isDir(regDirPath), "Test against regular directory");
        assertFalse(FileUtils.isDir(nonExistDirPath), "Test against nonExistent directory");
    }

    @Test
    void copyFile() throws IOException, ChecksumGenerationException {
        FileUtils.copyFile(COPY_SOURCE_FILE_1, COPY_DEST_FILE_1);
        String sourceChecksum = FileUtils.checksum(COPY_SOURCE_FILE_1);
        String destChecksum = FileUtils.checksum(COPY_DEST_FILE_1);
        assertEquals(sourceChecksum, destChecksum);
    }

    @Test
    void moveFile() throws ChecksumGenerationException, IOException {
        String sourceChecksum = FileUtils.checksum(COPY_SOURCE_FILE_1);
        FileUtils.rename(COPY_SOURCE_FILE_1, COPY_DEST_FILE_1);
        assertFalse(new File(COPY_SOURCE_FILE_1).exists());
        String destChecksum = FileUtils.checksum(COPY_DEST_FILE_1);
        assertTrue(new File(COPY_DEST_FILE_1).exists());
        assertEquals(sourceChecksum, destChecksum);
    }

    @Test
    void ensureDirectoryForce() {
        assertDoesNotThrow(() -> FileUtils.ensureDirectoryForce(BLOCK_FILE_PATH));
        File dir = new File(BLOCK_FILE_PATH);
        assertTrue(dir.isDirectory());
    }

    @Test
    void ensureDirectoryCreatesNonExisting() throws EnsureDirectoryException {
        String dirPath = testStageDir.getAbsolutePath() + "/project";
        FileUtils.ensureDirectory(dirPath);
        File dir = new File(dirPath);
        assertTrue(dir.isDirectory());
    }

    @Test
    void ensureDirectoryHandlesExisting() {
        assertDoesNotThrow(() -> FileUtils.ensureDirectory(EXIST_DIR_PATH));
    }

    @Test
    void ensureDirectoryThrowsException() {
        assertThrows(EnsureDirectoryException.class, () -> FileUtils.ensureDirectory(BLOCK_FILE_PATH));
    }

    @Test
    void touchWorks() throws IOException, InterruptedException {
        String testFilePath = testStageDir.getAbsolutePath() + "/testTouchFile.txt";
        FileUtils.touch(testFilePath);
        File testFile = new File(testFilePath);
        assertTrue(testFile.exists(), "The file did not get created");
        long firstModified = testFile.lastModified();
        long firstLength = testFile.length();
        Thread.sleep(1000);
        FileUtils.touch(testFilePath);
        long afterTouchModified = testFile.lastModified();
        long afterTouchLength = testFile.length();
        assertTrue(afterTouchModified >  firstModified, "The file modification time did not change");
        assertEquals(firstLength, afterTouchLength, "The length changed after touch...should not ever change contents of existing file");
    }

    @Test
    void appendFileWorks() throws IOException {
        String testFilePath = testStageDir.getAbsolutePath() + "/testAppendFile.txt";
        FileUtils.touch(testFilePath);
        FileUtils.append(testFilePath, "a", false);
        assertEquals(1.0, FileUtils.sizeBytes(testFilePath));
        FileUtils.delete(testFilePath);
        FileUtils.touch(testFilePath);
        FileUtils.append(testFilePath, "a", true);
        assertEquals(1.0, FileUtils.sizeBytes(testFilePath));
        FileUtils.append(testFilePath, "bb", true);
        assertEquals(4.0, FileUtils.sizeBytes(testFilePath), "The file size after append is not correct");
        FileUtils.append(testFilePath, "cc", false);
        assertEquals(6.0, FileUtils.sizeBytes(testFilePath), "The file size after append is not correct");
    }

    @Test
    void deleteDirectoryWorks() throws IOException {
        File testDir = new File(testStageDir.getAbsolutePath() + "/testdir");
        File subDir = new File(testDir.getAbsolutePath() + "/subdir");
        String testFilePath = testDir.getAbsolutePath() + "/file.txt";
        String subFilePath = subDir.getAbsolutePath() + "/subfile.txt";
        if (subDir.mkdirs()) {
            FileUtils.touch(testFilePath);
            FileUtils.touch(subFilePath);
            assertTrue(new File(testFilePath).exists());
            assertTrue(new File(subFilePath).exists());
            FileUtils.deleteDirectory(testDir.getAbsolutePath());
            assertFalse(testDir.exists());
        } else {
            fail("Unable to create test directory -> " + subDir.getAbsolutePath());
        }
    }

    @Test
    void getAllDirectoryFilesWorks() throws IOException {
        String testDir = testStageDir.getAbsolutePath() + "/testdir";
        setupDirGetFilesTests(testDir);
        List<File> allFiles = FileUtils.getDirectoryFiles(testDir);
        assertEquals(allFiles.size(), 3);
    }

    @Test
    void getDirectoryFilesMatchingWorks() throws IOException {
        String testDir = testStageDir.getAbsolutePath() + "/testdir";
        setupDirGetFilesTests(testDir);
        List<File> txtFiles = FileUtils.getDirectoryFiles(testDir, ".txt");
        assertEquals(txtFiles.size(), 2);
        List<File> csvFiles = FileUtils.getDirectoryFiles(testDir, ".csv");
        assertEquals(csvFiles.size(), 1);
        List<File> files1 = FileUtils.getDirectoryFiles(testDir, "1");
        assertEquals(files1.size(), 2);
    }

    @Test
    void zipFileWorks() throws IOException {
        String zipDirPath = testStageDir.getAbsolutePath() + "/zipDir/";
        String[] files = new String[]{ zipDirPath + "file1.txt", zipDirPath + "file2.txt", zipDirPath + "file3.txt"};
        setupZipMethodTests(zipDirPath, files);
        List<File> dirFiles = FileUtils.getDirectoryFiles(zipDirPath);
        assertEquals(dirFiles.size(), files.length, "The setup did not setup the correct number of files");
        File fileBeforeZip = new File(files[0]);
        long origLen = fileBeforeZip.length();
        FileUtils.zipFile(files[0]);
        File zipFile = new File(zipDirPath + "file1.zip");
        assertTrue(zipFile.exists(), "Zip file did not get created");
        assertTrue(origLen > zipDirPath.length(), "Zip file is not less than original file");
    }

    @Test
    void zipFilesWorks() throws IOException {
        String zipDirPath = testStageDir.getAbsolutePath() + "/zipDir/";
        String[] files = new String[]{ zipDirPath + "file1.txt", zipDirPath + "file2.txt", zipDirPath + "file3.txt"};
        setupZipMethodTests(zipDirPath, files);
        String multiZipFilename = "allFiles.zip";
        FileUtils.zipFiles(Arrays.asList(files), zipDirPath, multiZipFilename);
        long filesTotalLength = 0;
        for (String fp : files) {
            File file = new File(fp);
            filesTotalLength += file.length();
        }
        File zipFile = new File(zipDirPath, multiZipFilename);
        assertTrue(zipFile.exists());
        assertTrue(zipFile.length() < filesTotalLength, "multipart file is bigger than total file size");
    }

    @Test
    void zipDirWorks() throws IOException {
        String zipDirPath = testStageDir.getAbsolutePath() + "/zipDir/";
        String[] subdirs = new String[]{zipDirPath + "subdir1", zipDirPath + "subdir2", zipDirPath + "subdir3"};
        List<String> nestedDirs = new ArrayList<>();

        for (String sd: subdirs) {

            File f = new File(sd);
            f.mkdirs();

            for (int i=0; i < 10; i++) {
                String dirFilename = sd + "/file" + i + ".txt";
                File dirFile = new File(dirFilename);
                if (dirFile.createNewFile()) {
                    for (int j=0; j < 50; j++) {
                        FileUtils.append(dirFilename, "test file entry " + j, true);
                    }
                }

            }

            int subdirIdx = Integer.parseInt(sd.substring(sd.length() - 1));
            IntStream.range(0, subdirIdx + 1).forEach(n -> nestedDirs.add(sd + "/nestedDir" + n));

        }

        for (String nd : nestedDirs) {
            File nestedDir = new File(nd);
            nestedDir.mkdirs();
            int nestDirInt = Integer.parseInt(nd.substring(nd.length() - 1));
            if ( nestDirInt> 0) {
                for (int fn=1; fn < nestDirInt + 1; fn++) {
                    String nestedFilename = nd + "/file" + fn + ".txt";
                    FileUtils.touch(nestedFilename);
                    String[] writeEntries = new String[fn];
                    IntStream.range(1, fn + 1).forEach(wen -> writeEntries[wen - 1] = "entry:" + wen);
                    for (String entry: writeEntries) {
                        FileUtils.append(nestedFilename, entry, false);
                    }
                }
            }
        }

        FileUtils.zipDirectory(zipDirPath);
        File zipFile = new File(new File(zipDirPath).getParentFile().getAbsolutePath() + "/zipDir.zip");
        assertTrue(zipFile.exists());

        // Now lets unpack and see if it has all original contents
        FileUtils.deleteDirectory(zipDirPath);
        FileUtils.unzip(zipFile.getAbsolutePath());
        File unzippedDir = new File(zipDirPath);
        assertTrue(unzippedDir.isDirectory(), "Unzipped failed");
        for (String subdir : subdirs) {
            if (!FileUtils.isDir(zipDirPath)) {
                fail("The file did not uncompress to correct location");
            }
        }
        assertEquals(3, FileUtils.getDirectoryFiles(zipDirPath).size());
        assertEquals(12, FileUtils.getDirectoryFiles(zipDirPath + "subdir1").size());
        assertEquals(0, FileUtils.getDirectoryFiles(zipDirPath + "nestedDir0").size());
    }

    @Test
    void sizeBytesWorks() throws IOException {
        String testFilePath = testStageDir + "/sizeFileTest.txt";
        FileWriter fw = new FileWriter(testFilePath);
        fw.write("a".repeat(256));
        fw.close();
        double sizeInB = FileUtils.sizeBytes(testFilePath);
        assertEquals(256.0, sizeInB);
    }

    @Test
    void sizeKBytesWorks() throws IOException {
        String testFilePath = testStageDir + "/sizeFileTest.txt";
        FileWriter fw = new FileWriter(testFilePath);
        fw.write("a".repeat(2058));
        fw.close();
        double sizeInKB = FileUtils.sizeKB(testFilePath);
        assertEquals(2.009765625, sizeInKB);
    }

    @Test
    void sizeMBytesWorks() throws IOException {
        String testFilePath = testStageDir + "/sizeFileTest.txt";
        FileWriter fw = new FileWriter(testFilePath);
        fw.write("a".repeat(4 * (1024 * 1024)));
        fw.close();
        double sizeInMB = FileUtils.sizeMB(testFilePath);
        assertEquals(4.0, sizeInMB);
    }

//    @Test
//    void sizeGBytesWorks() throws IOException {
//        String testFilePath = testStageDir + "/sizeFileTest.txt";
//        FileWriter fw = new FileWriter(testFilePath);
//        fw.write("a".repeat(1024*1024*1024));
//        fw.close();
//        double sizeInGB = FileUtils.sizeGB(testFilePath);
//        assertEquals(1.0, sizeInGB);
//    }

    private void setupDirGetFilesTests(String baseDirPath) throws IOException {
        File testDir = new File(testStageDir.getAbsolutePath() + "/testdir");
        String[] files = new String[]{testDir.getAbsolutePath() + "/file1.txt",
                testDir.getAbsolutePath() + "/file2.txt",
                testDir.getAbsolutePath() + "/file1.csv"};
        if (testDir.mkdirs()) {
            for (String fp : files) {
                FileUtils.touch(fp);
            }
        } else {
            fail("Unable to create test dir -> " + testDir.getAbsolutePath());
        }
    }

    private void setupZipMethodTests(String zipDirPath, String[] stageFiles) throws IOException {
        FileUtils.ensureDirectory(zipDirPath);
        for (String fp : stageFiles) {
            FileUtils.touch(fp);
            String fileName = fp.substring(fp.lastIndexOf("/") + 1);
            for (int n = 0; n < 500; n++) {
                FileUtils.append(fp, fileName + ":" + n, true);
            }
        }

    }

}
