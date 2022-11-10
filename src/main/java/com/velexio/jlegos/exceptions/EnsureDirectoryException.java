package com.velexio.jlegos.exceptions;

import java.io.File;
import java.io.IOException;

/**
 * Exceptions to indiate errors for FileUtils.ensureDirectory method
 */
public class EnsureDirectoryException extends IOException {

    /**
     * Throws exception with default error message
     */
    public EnsureDirectoryException() {
        super("A regular file exists where directory would be placed and could not be overwritten");
    }

    /**
     * Thrown when regular file exists with same name as passed directory
     *
     * @param directoryFile
     */
    public EnsureDirectoryException(File directoryFile) {
        super("A regular file already exists where directory [" + directoryFile.getAbsolutePath() + "] is desired. Use force method to overwrite");
    }

    /**
     * Provides override of default message
     *
     * @param message
     */
    public EnsureDirectoryException(String message) {
        super(message);
    }

}
