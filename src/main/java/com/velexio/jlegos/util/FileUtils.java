package com.velexio.jlegos.util;

import com.velexio.jlegos.exceptions.ChecksumGenerationException;
import com.velexio.jlegos.exceptions.EnsureDirectoryException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Contains some helpful utility methods for handling file operations.
 */
@Slf4j
public class FileUtils {
    private static final int MAX_COPY_BUFFER = 8388608;
    private static final int MIN_COPY_BUFFER = 4;

    private static int copyBufferSize = 4096;

    /**
     * Will change the copy operations (includes move) buffer size to a custom value.
     * @param newBufferSize Taks int value to indicate the size of the buffer (in bytes) to use for further operations.  Default 4k, min 512 (bytes).
     */
    public static void changeCopyBufferSize(int newBufferSize) {
        if (newBufferSize < MAX_COPY_BUFFER) {
            copyBufferSize = Math.max(newBufferSize, MIN_COPY_BUFFER);
        } else {
            copyBufferSize = MAX_COPY_BUFFER;
        }
    }

    /**
     * Will retrieve the current value of the copyBufferSize value begin used for copy/move operations
     * @return int value that indicates the size of the buffer (in bytes)
     */
    public static int getCopyBufferSize() {
        return copyBufferSize;
    }

    /**
     * Will copy file.
     * @param sourcePath A string representing the path of the file to be copied
     * @param destPath A string representing the path of the destination file
     * @throws IOException In event that either the source file/dest file cannot be created or closed.
     */
    public static void copyFile(String sourcePath, String destPath) throws IOException {
        FileInputStream fis = new FileInputStream(sourcePath);
        FileOutputStream fos = new FileOutputStream(destPath);
        int bytesRead;
        byte[] buffer = new byte[copyBufferSize];
        while ((bytesRead = fis.read(buffer)) > -1) {
            fos.write(buffer, 0, bytesRead);
        }
        fis.close();
        fos.close();
    }

    /**
     * Will move a file.  Equivalent to a rename operation.
     *
     * @param currentPath String representing the path of the current file
     * @param newPath     String to represent the new name of the file
     * @throws IOException If there is an issue during the move operation (i.e. permissions, invalid new path, etc)
     */
    public static void moveFile(String currentPath, String newPath) throws IOException {
        FileUtils.copyFile(currentPath, newPath);
        File sourceFile = new File(currentPath);
        if (!sourceFile.delete()) {
            throw new IOException("Unable to delete original file during \"move operation\". Check original file permissions.  " +
                    "Effective operation was a copy");
        }
    }

    /**
     * A nice utility if you need to make sure a directory exists and if not, have it created.
     * This method will overwrite if a regular file exists where directory is intended to go.
     * Use the "ensureDirectory" method if you want a safe method.
     * @param directoryPath The path where the directory should exist
     * @throws EnsureDirectoryException if the underlying regular file could not be removed.
     */
    public static void ensureDirectoryForce(String directoryPath) throws EnsureDirectoryException {
        String blockingFileErrMessage = "A regular file exists where directory [" +
                directoryPath + "] would be placed and could not be overwritten";
        try {
            FileUtils.ensureDirectory(directoryPath);
        } catch (EnsureDirectoryException fbdce) {
            File file = new File(directoryPath);
            if (!file.delete()) {
                throw new EnsureDirectoryException(blockingFileErrMessage);
            } else {
                try {
                    FileUtils.ensureDirectory(directoryPath);
                } catch (EnsureDirectoryException nfbdce) {
                    throw new EnsureDirectoryException(blockingFileErrMessage);
                }
            }
        }
    }

    /**
     * A utility for ensuring that a directory exists, and if not, will attempt to create.
     *
     * @param directoryPath The path where the directory is to be created
     * @throws EnsureDirectoryException If there is a regular file present that matches the directory path
     */
    public static void ensureDirectory(String directoryPath) throws EnsureDirectoryException {
        File dir = new File(directoryPath);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
              throw new EnsureDirectoryException(dir);
            }
        } else {
            if (!dir.mkdirs()) {
                throw new EnsureDirectoryException("A regular file exists where directory [" +
                        directoryPath + "] would be placed and could not be overwritten");
            }
        }
    }

    /**
     * Quick utility that is both typesafe and easier to remember then using System.getProperty("java.io.tmpdir").
     *
     * @return String that represents path to os specific temp directory
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Simple touch command to either create an empty file if it does not exist, or to change the last modified date if it does exits.
     * Meant to work similar to the unix touch command.
     *
     * @param filePath The path to either where you want new empty file created or path to existing file to touch
     * @throws IOException On issue creating/modifying the file
     */
    public static void touchFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                FileWriter fw = new FileWriter(file);
                fw.write("");
                fw.close();
            }
        } else {
            Date nowDate = new Date();
            if (!file.setLastModified(nowDate.getTime())) {
                throw new IOException("Unable to 'touch' existing file [ " + file.getAbsolutePath() + "]. Check permissions and try again");
            }
        }
    }

    /**
     * Simple utitly to make easier for simple append to file operations.  Less boilerplate than creating FileWriter, remembering to close, etc.
     *
     * @param filePath    String representation of the path to the file you want to append to
     * @param appendValue String value that you want appended to the file
     * @throws IOException Will be raised if there is an issue writing to the file
     */
    public static void append(String filePath, String appendValue) throws IOException {
        FileWriter fw = new FileWriter(new File(filePath));
        fw.append(appendValue);
        fw.close();
    }

    /**
     * Generates checksum of the file. Default algorithm is SHA-512. If you need a different one, use the overloaded method that allows you to
     * specify an algorithm
     *
     * @param filePath The path to the file that checksum will be performed on
     * @return String representing the checksum value
     * @throws ChecksumGenerationException if there is IO issues
     */
    public static String checksum(String filePath) throws ChecksumGenerationException {
        return checksum(filePath, "SHA-512");
    }

    /**
     * Generates a checksum of file represented by the passed path String
     *
     * @param filePath        The string path representing the location of the file to checksum
     * @param digestAlgorithm The algorithm to use. Needs to be a valid algorithm that the standard java.security.MessageDigest class will accept
     * @return A String object representing the checksum value
     * @throws ChecksumGenerationException If there is either IO issues or passed an invalid MessageDigest algorithm
     */
    public static String checksum(String filePath, String digestAlgorithm) throws ChecksumGenerationException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            FileInputStream fis = new FileInputStream(filePath);
            byte[] byteArr = new byte[4096];
            int byteCount;
            while ((byteCount = fis.read(byteArr)) > -1) {
                digest.update(byteArr, 0, byteCount);
            }
            fis.close();
            byte[] csBytes = digest.digest();
            return IntStream.range(0, csBytes.length)
                    .mapToObj(i -> Integer.toString((csBytes[i] & 0xff) + 0x100, 16).substring(1))
                    .collect(Collectors.joining());
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new ChecksumGenerationException();
        }
    }

    /**
     * Removes a directory in a recursive fashion. Meaning it will not only remove non-empty directories (unlike file.delete), but it will also
     * follow any sub-directories and remove those as well.
     *
     * @param directoryPath A string representing the path to the directory that is to be removed
     * @throws IOException If any of the files/sub-dirs cannot be deleted.
     */
    public static void deleteDirectory(String directoryPath) throws IOException {
        File d = new File(directoryPath);
        List<String> delinquentList = new ArrayList<>();
        if (d.isDirectory()) {
            String[] fileList = d.list();
            if (fileList != null) {
                for (String fp : fileList) {
                    File f = new File(directoryPath + "/" + fp);
                    if (f.isDirectory()) {
                        FileUtils.deleteDirectory(f.getAbsolutePath());
                    } else {
                        if (!f.delete()) {
                            delinquentList.add(f.getAbsolutePath());
                        }
                    }
                }
            }
            if (!d.delete()) {
                delinquentList.add(d.getAbsolutePath());
            }
        }
        if (delinquentList.size() > 0) {
            throw new IOException(getDelinquentListErrorMessage(delinquentList));
        }
    }

    /**
     * This simply empties the contents of the directory, but DOES NOT remove the top level directory represented by the passed in path.  Note
     * it will remove all sub-directories
     *
     * @param directoryPath The path to the directory to empty
     * @throws IOException If there is an issue removing a file and/or subdirectory
     */
    public static void emptyDirectory(String directoryPath) throws IOException {
        File d = new File(directoryPath);
        List<String> delinquentList = new ArrayList<>();
        if (d.isDirectory()) {
            String[] dirFileList = d.list();
            if (dirFileList != null) {
                for (String fp : dirFileList) {
                    File f = new File(fp);
                    if (f.isDirectory()) {
                        try {
                            FileUtils.deleteDirectory(f.getAbsolutePath());
                        } catch (IOException ioe) {
                            delinquentList.add(f.getAbsolutePath());
                        }
                    } else {
                        if (!f.delete()) {
                            delinquentList.add(f.getAbsolutePath());
                        }
                    }
                }
            }
        }
        if (delinquentList.size() > 0) {
            throw new IOException(getDelinquentListErrorMessage(delinquentList));
        }
    }

    /**
     * Gets the files of the specified directory (Including subdirectories).
     *
     * @param directoryPath The string representation of the path to the directory
     * @return A List of File objects representing the directories contents. Note, only first-level contents are retrieved
     */
    public static List<File> getDirectoryFiles(String directoryPath) {
        List<File> fileList = new ArrayList<>();
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            fileList = Arrays.asList(d.listFiles());
        }
        return fileList;
    }

    /**
     * Will get all the first level files from directory that match the specified pattern. The pattern is just a simple string matching pattern.
     * This method will not recognize regex, so just inlcude a simple string that will match the file.
     * <p>
     * Example:
     * .txt will match all files that contain a .txt (file1.txt, file2.txt and    file.txt.old
     *
     * @param directoryPath The path to the directory holding files
     * @param matchPattern  A simple string match that will be matched against any portion of the file name
     * @return A List of File objects that represents all the files matching the pattern
     */
    public static List<File> getDirectoryFiles(String directoryPath, String matchPattern) {
        List<File> fileList = new ArrayList<>();
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            File[] files = d.listFiles((dir, name) -> name.contains(matchPattern));
            if (files != null) {
                fileList = Arrays.asList(files);
            }
        }
        return fileList;
    }

    private static String getDelinquentListErrorMessage(List<String> delinquentList) {
        String newLine = System.lineSeparator();
        StringBuilder errMessage = new StringBuilder();
        errMessage.append("Unable to remove the following files/subdirectories.  Check permissions and try again:");
        errMessage.append(newLine);
        for (String fileName : delinquentList) {
            errMessage.append(fileName);
            errMessage.append(newLine);
        }
        return errMessage.toString();
    }

}
