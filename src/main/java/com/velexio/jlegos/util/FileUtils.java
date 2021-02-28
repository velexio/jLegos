package com.velexio.jlegos.util;

import com.velexio.jlegos.exceptions.ChecksumGenerationException;
import com.velexio.jlegos.exceptions.EnsureDirectoryException;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Contains some helpful utility methods for handling file operations.
 */
public class FileUtils {
    private static final int MAX_COPY_BUFFER = 8388608;
    private static final int MIN_COPY_BUFFER = 4;
    private static final int MAX_ZIP_BUFFER = 4194304;
    private static final int MIN_ZIP_BUFFER = 1024;

    private static int copyBufferSize = 4096;
    private static int zipBufferSize = 16384;

    private static byte[] copyBuffer = new byte[copyBufferSize];
    private static byte[] zipBuffer = new byte[zipBufferSize];

    /**
     * <p>
     * Will change the copy operations (includes move) buffer size to a custom value.
     * </p>
     *
     * @param newBufferSize Tasks int value to indicate the size of the buffer (in bytes) to use for further operations.  Default 4k, min 512 (bytes).
     */
    public static void changeCopyBufferSize(int newBufferSize) {
        if (newBufferSize < MAX_COPY_BUFFER) {
            copyBufferSize = Math.max(newBufferSize, MIN_COPY_BUFFER);
        } else {
            copyBufferSize = MAX_COPY_BUFFER;
        }
        copyBuffer = new byte[copyBufferSize];
    }

    /**
     * Will change the buffer size used during zip* operations.  Default is 16k (16384)
     * @param newSize The new size to use
     */
    public static void setZipBufferSize(int newSize) {
        if (newSize < MAX_ZIP_BUFFER) {
            zipBufferSize = Math.max(newSize, MIN_ZIP_BUFFER);
        } else {
            zipBufferSize = MAX_ZIP_BUFFER;
        }
        zipBuffer = new byte[zipBufferSize];
    }

    /**
     * Will retrieve the current value of the copyBufferSize value begin used for copy/move operations
     * @return int value that indicates the size of the buffer (in bytes)
     */
    public static int getCopyBufferSize() {
        return copyBufferSize;
    }

    public static boolean exists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Determines if the file at path is a directory
     *
     * @param dirPath The path to the directory to check
     * @return boolean
     */
    public static boolean isDir(String dirPath) {
        File file = new File(dirPath);
        return file.isDirectory();
    }

    /**
     * Determines if the file at path is a file (not a directory)
     *
     * @param filePath The path to the file to check
     * @return boolean
     */
    public static boolean isFile(String filePath) {
        File file = new File(filePath);
        return file.isFile();
    }

    /**
     * Determines if the file at path is a hidden file
     *
     * @param filePath The path to the file to check
     * @return boolean
     */
    public static boolean isHidden(String filePath) {
        File file = new File(filePath);
        return file.isHidden();
    }

    /**
     * Returns boolean based on if file is writable by process
     *
     * @param filePath String path to the file
     * @return boolean
     */
    public static boolean isWriteable(String filePath) {
        File file = new File(filePath);
        return isWriteable(file);
    }

    /**
     * Returns boolean based on if file is writable by process
     *
     * @param file File object to check
     * @return boolean
     */
    public static boolean isWriteable(File file) {
        return file.canWrite();
    }

    /**
     * Returns boolean based on if file is readable by process
     *
     * @param filePath String path to the file
     * @return boolean
     */
    public static boolean isReadable(String filePath) {
        File file = new File(filePath);
        return isReadable(file);
    }

    /**
     * Returns boolean base on if file is readable by process
     *
     * @param file File object to check
     * @return boolean
     */
    public static boolean isReadable(File file) {
        return file.canRead();
    }

    /**
     * Will copy file.
     * @param source A string representing the path of the file to be copied
     * @param dest A string representing the path of the destination file
     * @param options Optional varargs that can be one or more of the {@link com.velexio.jlegos.util.FileCopyOption} enum values. If no options are
     *                specified, a default of FileCopyOption.COPY_ATTRIBUTES will be applied.
     * @throws IOException Occurs when I/O operation fails
     * @throws FileNotFoundException If the source file is not present or not a file object
     * @see com.velexio.jlegos.util.FileCopyOption
     */
    public static void copyFile(String source, String dest, FileCopyOption ... options) throws IOException {
        if (!isFile(source)) {
            throw new FileNotFoundException("The file [ " + source + "] does not exist.  Copy terminated");
        }
        List<CopyOption> copyOptions = new ArrayList<>(List.of(StandardCopyOption.COPY_ATTRIBUTES));
        if (options.length > 0) {
            copyOptions = new ArrayList<>();
            for (FileCopyOption option : options) {
                copyOptions.add(option.getNioEquiv());
            }
        }
        CopyOption[] finalCopyOptions = new CopyOption[copyOptions.size()];
        finalCopyOptions = copyOptions.toArray(finalCopyOptions);
        Path sourcePath = Paths.get(source);
        Path destPath = Paths.get(dest);
        Files.copy(sourcePath, destPath, finalCopyOptions);
    }

    /**
     * <p>
     * Simple wrapper around file rename, but convenience of not having to initialize File objects.
     * </p>
     *
     * @param currentPath String representing the path of the current file
     * @param newPath     String to represent the new name of the file
     */
    public static void rename(String currentPath, String newPath) {
        File source = new File(currentPath);
        File dest = new File(newPath);
        source.renameTo(dest);
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
        } catch (EnsureDirectoryException ede) {
            File file = new File(directoryPath);
            if (!file.delete()) {
                throw new EnsureDirectoryException(blockingFileErrMessage);
            } else {
                try {
                    FileUtils.ensureDirectory(directoryPath);
                } catch (EnsureDirectoryException nede) {
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
     * Quick utility that is both type-safe and easier to remember then using System.getProperty("java.io.tmpdir").
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
    public static void touch(String filePath) throws IOException {
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
     * <p>
     *  Simple delete for a single file.
     * </p>
     *
     * @param filePath The string that represents the path to the file
     * @throws IOException Thrown if there is an IO issue during delete
     */
    public static void delete(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.delete()) {
            throw new IOException("Unable to delete file [ " + filePath + "]. Verify file exists and permissions set correctly");
        }
    }

    /**
     * Simple utility to make easier for simple append to file operations.  Less boilerplate than creating FileWriter, remembering to close, etc.
     *
     * @param filePath    String representation of the path to the file you want to append to
     * @param appendValue String value that you want appended to the file
     * @param asNewLine Set as true if the appended value should be set on a new line in the file. False will just append to the end of the file.
     * @throws IOException Will be raised if there is an issue writing to the file
     */
    public static void append(String filePath, String appendValue, boolean asNewLine) throws IOException {
        File appendFile = new File(filePath);
        FileWriter fw = new FileWriter(appendFile, true);
        if (asNewLine && appendFile.length() > 0) {
            appendValue = System.lineSeparator() + appendValue;
        }
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
            MessageDigest digest = MessageDigest.getInstance(digestAlgorithm);
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
     * <p>
     *  Will create a directory at location specified by the parameter.  If the parent directories do not exist, then they will be created. Similar to
     *  linux command "mkdir -p dir path"
     * </p>
     *
     * @param dirPath The path where the directory will be created
     * @throws IOException If there is an IO error during operation
     */
    public static void createDirectory(String dirPath) throws IOException {
        File dir = new File(dirPath);
        if (!dir.mkdirs()) {
            throw new IOException("Unable to create directory");
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
     * This method will not recognize regex, so just include a simple string that will match the file.
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

    /**
     * Will unzip a compressed file inside it's parent folder.
     * @param zipFilePath The path to the file that is to be unzipped
     * @throws IOException Thrown if there is an issue reading / writing during the operation
     */
    public static void unzip(String zipFilePath) throws IOException {
        File zipfileParentDir = new File(zipFilePath).getParentFile();
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File entryFile = new File(zipfileParentDir, zipEntry.getName());
            if (!entryFile.getCanonicalPath().startsWith(zipfileParentDir.getCanonicalPath() + FileUtils.getFileSeparator())) {
                throw new IOException("Zip entry [ " + zipEntry.getName() + "] is outside the target directory...unable to uncompress");
            }
            if (zipEntry.getName().endsWith("/")) {
                FileUtils.ensureDirectoryForce(entryFile.getAbsolutePath());
            } else {
                FileOutputStream fos = new FileOutputStream(entryFile);
                int len;
                while ((len = zis.read(zipBuffer)) > 0) {
                    fos.write(zipBuffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

    }


    /**
     * <p>
     * Takes a file path and compresses the file with same file name, adding the .zip file extension. If the file has an existing extension, it is
     * replaced with the .zip. Meaning if file named file1.txt, then file1.zip will be compressed filename.  If no extension,
     * the .zip is added  i.e. file1 will just have ".zip" added file1.zip.
     * </p>
     * <p>
     * Note: An extension is considered to be any alphanumeric value that is 2-3 chars in length, proceeded by period "." character.
     * </p>
     *
     * @param filePath A string representation of the path to the file
     * @throws IOException If the file does not exist, cannot be read or the containing directory does not allow writes
     */
    public static void zipFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File [ " + filePath + "] does not exist");
        }
        String parentDir = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("/") + 1);
        String zipFilename = file.getName() + ".zip";
        String matchPattern = "([\\S\\s]+)\\.([\\w]{2,3}$)";
        if (file.getName().matches(matchPattern)) {
            zipFilename = file.getName().replaceAll(matchPattern, "$1.zip" );
        }
        String fullZipPath = parentDir + zipFilename;
        FileInputStream fis = new FileInputStream(file);
        FileOutputStream fos = new FileOutputStream(fullZipPath);
        ZipOutputStream zos = new ZipOutputStream(fos);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);
        int len;
        while ((len = fis.read(zipBuffer)) > -1) {
            zos.write(zipBuffer, 0, len);
        }
        zos.close();
        fis.close();
        fos.close();
        if (new File(fullZipPath).exists()) {
            file.delete();
        }
    }

    /**
     * This method will create a multi-part file that contains multiple files.  Source files do not need to be in same location. You need to specify
     * the name of the zip file as well as the destination directory.
     * @param zipFiles A List of String objects that represent the path to the files that are to be included
     * @param destDirectoryPath String that represents the full path to the directory where the zip file will be created
     * @param zipFilename The name for the multi-part zip file. i.e. allFiles.zip, my-project.zip
     * @throws IOException If the path that represents the destination directory either does not exist or is not a directory
     */
    public static void zipFiles(List<String> zipFiles, String destDirectoryPath, String zipFilename) throws IOException {
        File dir = new File(destDirectoryPath);
        if (!dir.isDirectory()) {
            throw new IOException("The path provided [ " + destDirectoryPath + "] either does not exist or not a directory.");
        }
        String multiZipFilename = destDirectoryPath + "/" + zipFilename;
        FileOutputStream fos = new FileOutputStream(multiZipFilename);
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (String srcFilePath : zipFiles) {
            File file = new File(srcFilePath);
            FileInputStream fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            int len;
            while ((len = fis.read(zipBuffer)) > -1 ) {
               zos.write(zipBuffer, 0, len);
            }
            fis.close();
        }
        zos.close();
        fos.close();
    }

    /**
     * <p>
     *  Will compress an entire directory. The compressed file will be placed in the parent directory and will be named with the same name, with the
     *  ".zip" extension. Original directory object will remain intact.
     * </p>
     * * <p>
     *  <b>Note:</b> Any hidden files will not be included in the zip file
     * </p>
     *
     * @param directoryPath A string object representing the path to the directory to be compressed
     * @throws IOException Will be thrown if path does not represent a directory or any other IO error occurs
     */
    public static void zipDirectory(String directoryPath) throws IOException {
        File dir = new File(directoryPath);
        String zipDirFilename = dir.getParentFile().getAbsolutePath() + "/" + dir.getName() + ".zip";
        if (!dir.isDirectory()) {
            throw new IOException("The directory path provided [ " + directoryPath
                    + "] either does not exist or not a directory object. Unable to proceed with compression routine.");
        }
        FileOutputStream fos = new FileOutputStream(zipDirFilename);
        ZipOutputStream zos = new ZipOutputStream(fos);
        zipDirFile(dir, dir.getName(), zos);
        zos.close();
        fos.close();
    }

    /*
    Recursive method for compressing all of directory contents
     */
    private static void zipDirFile(File fileToZip, String fileName, ZipOutputStream zipOutStream) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOutStream.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOutStream.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOutStream.closeEntry();
            File[] childFiles = fileToZip.listFiles();
            for (File file : childFiles) {
                zipDirFile(file, fileName + "/" + file.getName(), zipOutStream);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOutStream.putNextEntry(zipEntry);
        int length;
        while ((length = fis.read(zipBuffer)) > -1 ) {
            zipOutStream.write(zipBuffer, 0, length);
        }
        fis.close();
    }

    /**
     * <p>
     * Will return the size of the file in gigabytes
     * </p>
     * @param filePath The string that represents the path to the file
     * @return Size of the file
     * @throws IOException If the file does not exist or is not a regular file
     */
    public static double sizeGB(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File [ " + filePath + "] is not a regular file.");
        }
        return (double) file.length() / (1024 * 1024 * 1024);
    }

    /**
     *  <p>
     *  Will return the size of the specified file in megabytes
     * </p>
     *
     * @param filePath The path of the file to get size
     * @return Size of the file in megabytes
     * @throws IOException If the file does not exist or is not a regular file
     */
    public static double sizeMB(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File [ " + filePath + "] is not a regular file.");
        }
        return (double) file.length() / (1024 * 1024);
    }

    /**
     *  <p>
     *  Will return the size of the specified file in kilobytes
     * </p>
     *
     * @param filePath The path of the file to get size
     * @return Size of the file in kilobytes
     * @throws IOException If the file does not exist or is not a regular file
     */
    public static double sizeKB(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File [ " + filePath + "] is not a regular file.");
        }
        return (double) file.length() / 1024;
    }

    /**
     *  <p>
     *  Will return the size of the specified file in bytes
     * </p>
     *
     * @param filePath The path of the file to get size
     * @return Size of the file in bytes
     * @throws IOException If the file does not exist or is not a regular file
     */
    public static double sizeBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File [ " + filePath + "] is not a regular file.");
        }
        return (double) file.length();
    }

    /**
     * Simple helper method to get os dependent file separator for those of us that cannot seem to remember the System.getProperty name.
     * @return The OS dependent file separator
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /*
    A dry method to de-duplicate code used in emptyDirectory methods
     */
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
