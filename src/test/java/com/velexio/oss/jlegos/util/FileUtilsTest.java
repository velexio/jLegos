package com.velexio.oss.jlegos.util;

import com.velexio.oss.jlegos.exceptions.ChecksumGenerationException;
import com.velexio.oss.jlegos.exceptions.EnsureDirectoryException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilsTest {

    private File testStageDir = new File(FileUtils.getTempDir() + "/file-utils-test-stage");
    private final String BLOCK_FILE_PATH = testStageDir.getAbsolutePath() + "/block-file";
    private final String EXIST_DIR_PATH = testStageDir.getAbsolutePath() + "/existing-dir";
    private final String COPY_SOURCE_FILE_1 = testStageDir.getAbsolutePath() + "/source.txt";
    private final String COPY_DEST_FILE_1 = testStageDir.getAbsolutePath() + "/dest.txt";

    @BeforeEach
    void setupEach() throws IOException {
        testStageDir.mkdirs();
        File existDir = new File(EXIST_DIR_PATH);
        existDir.mkdir();
        FileUtils.touchFile(BLOCK_FILE_PATH);
        FileUtils.append(COPY_SOURCE_FILE_1, "source");
    }

    @AfterEach
    void tearDownEach() {
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
    void copyFile() throws IOException, ChecksumGenerationException {
        FileUtils.copyFile(COPY_SOURCE_FILE_1, COPY_DEST_FILE_1);
        String sourceChecksum = FileUtils.checksum(COPY_SOURCE_FILE_1);
        String destChecksum = FileUtils.checksum(COPY_DEST_FILE_1);
        assertEquals(sourceChecksum, destChecksum);
    }

    @Test
    void moveFile() throws ChecksumGenerationException, IOException {
        String sourceChecksum = FileUtils.checksum(COPY_SOURCE_FILE_1);
        FileUtils.moveFile(COPY_SOURCE_FILE_1, COPY_DEST_FILE_1);
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
    void ensureDirectoryHandlesExisting() throws EnsureDirectoryException {
        assertDoesNotThrow(() -> FileUtils.ensureDirectory(EXIST_DIR_PATH));
    }

    @Test
    void ensureDirectoryThrowsException() {
        assertThrows(EnsureDirectoryException.class, () -> FileUtils.ensureDirectory(BLOCK_FILE_PATH));
    }

    @Test
    void deleteDirectoryWorks() throws IOException {
        File testDir = new File(testStageDir.getAbsolutePath() + "/testdir");
        File subDir = new File(testDir.getAbsolutePath() + "/subdir");
        String testFilePath = testDir.getAbsolutePath() + "/file.txt";
        String subFilePath = subDir.getAbsolutePath() + "/subfile.txt";
        subDir.mkdirs();
        FileUtils.touchFile(testFilePath);
        FileUtils.touchFile(subFilePath);
        assertTrue(new File(testFilePath).exists());
        assertTrue(new File(subFilePath).exists());
        FileUtils.deleteDirectory(testDir.getAbsolutePath());
        assertFalse(testDir.exists());
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

    private void setupDirGetFilesTests(String baseDirPath) throws IOException {
        File testDir = new File(testStageDir.getAbsolutePath() + "/testdir");
        String[] files = new String[]{testDir.getAbsolutePath() + "/file1.txt",
                testDir.getAbsolutePath() + "/file2.txt",
                testDir.getAbsolutePath() + "/file1.csv"};
        testDir.mkdirs();
        for (String fp : files) {
            FileUtils.touchFile(fp);
        }
    }
}
