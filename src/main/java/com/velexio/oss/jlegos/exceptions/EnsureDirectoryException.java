package com.velexio.oss.jlegos.exceptions;

import java.io.File;
import java.io.IOException;

public class EnsureDirectoryException extends IOException {

    public EnsureDirectoryException() {
        super("A regular file exists where directory would be placed and could not be overwritten");
    }

    public EnsureDirectoryException(File directoryFile) {
        super("A regular file already exists where directory ["+directoryFile.getAbsolutePath()+"] is desired. Use force method to overwrite");
    }

    public EnsureDirectoryException(String message) {
        super(message);
    }

}
