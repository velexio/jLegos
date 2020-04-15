package com.velexio.oss.jlegos.util;

import com.velexio.oss.jlegos.exceptions.ChecksumGenerationException;
import com.velexio.oss.jlegos.exceptions.EnsureDirectoryException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            copyBufferSize = newBufferSize >= MIN_COPY_BUFFER ? newBufferSize : MIN_COPY_BUFFER;
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
       int bytesRead = -1;
       byte[] buffer = new byte[copyBufferSize];
       while((bytesRead = fis.read(buffer)) != -1) {
           fos.write(buffer, 0, bytesRead);
       }
       fis.close();
       fos.close();
    }

    /**
     * Will move a file.  Equivalent to a rename operation.
     * @param currentPath String representing the path of the current file
     * @param newPath String to represent the new name of the file
     * @throws IOException
     */
    public static void moveFile(String currentPath, String newPath) throws IOException {
        FileUtils.copyFile(currentPath, newPath);
        File sourceFile = new File(currentPath);
        sourceFile.delete();
    }

    /**
     * A nice utility if you need to make sure a directory exists and if not, have it created.
     * This method will overwrite if a regular file exists where directory is intended to go.
     * Use the "ensureDirectory" method if you want a safe method.
     * @param directoryPath The path where the directory should exist
     * @throws EnsureDirectoryException if the underlying regular file could not be removed.
     */
    public static void ensureDirectoryForce(String directoryPath) throws EnsureDirectoryException {
       try {
           FileUtils.ensureDirectory(directoryPath);
       } catch (EnsureDirectoryException fbdce) {
           File file = new File(directoryPath);
           file.delete();
           try {
               FileUtils.ensureDirectory(directoryPath);
           } catch (EnsureDirectoryException nfbdce) {
               throw new EnsureDirectoryException("A regular file exists where directory [" +
                       directoryPath + "] would be placed and could not be overwritten");
           }
       }
    }

    /**
     * A utility for ensuring that a directory exists, and if not, will attempt to create.
     * @param directoryPath
     * @throws EnsureDirectoryException If there is a regular file present that matches the directory path
     */
    public static void ensureDirectory(String directoryPath) throws EnsureDirectoryException {
        File dir = new File(directoryPath);
        if (dir.exists()) {
            if (!dir.isDirectory()) {
              throw new EnsureDirectoryException(dir);
            }
        } else {
            dir.mkdirs();
        }
    }

    /**
     * Quick utility that is both typesafe and easier to remember then using System.getProperty("java.io.tmpdir").
     * @return String that represents path to os specific temp directory
     */
    public static String getTempDir() {
        return System.getProperty("java.io.tmpdir");
    }

    public static void touchFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileWriter fw = new FileWriter(file);
        fw.write("");
        fw.close();
    }

    public static void append(String filePath, String appendValue) throws IOException {
        FileWriter fw = new FileWriter(new File(filePath));
        fw.append(appendValue);
        fw.close();
    }

    public static String checksum(String filePath) throws ChecksumGenerationException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            FileInputStream fis = new FileInputStream(filePath);
            byte[] byteArr = new byte[4096];
            int byteCount = 0;
            while((byteCount = fis.read(byteArr)) > -1) {
                digest.update(byteArr, 0, byteCount);
            }
            fis.close();
            byte[] csBytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < csBytes.length; i++) {
                sb.append(Integer.toString((csBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new ChecksumGenerationException();
        }
    }

    public static void deleteDirectory(String directoryPath) {
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            String[] dfl = d.list();
            for (String fp : dfl) {
                File f = new File(directoryPath + "/" +fp);
                if (f.isDirectory()) {
                    FileUtils.deleteDirectory(f.getAbsolutePath());
                } else {
                    f.delete();
                }
            }
            d.delete();
        }
    }

    public static void emptyDirectory(String directoryPath) {
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            String[] dfl = d.list();
            for (String fp : dfl) {
                File f = new File(fp);
                if (f.isDirectory()) {
                    FileUtils.emptyDirectory(fp);
                } else {
                    f.delete();
                }
            }
        }
    }

    public static List<File> getDirectoryFiles(String directoryPath) {
        List<File> l = new ArrayList<>();
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            l = Arrays.asList(d.listFiles());
        }
        return l;
    }

    public static List<File> getDirectoryFiles(String directoryPath, String matchPattern) {
        List<File> l = new ArrayList<>();
        File d = new File(directoryPath);
        if (d.isDirectory()) {
            File [] files = d.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.contains(matchPattern);
                }
            });
            l = Arrays.asList(files);
        }
        return l;
    }

}
