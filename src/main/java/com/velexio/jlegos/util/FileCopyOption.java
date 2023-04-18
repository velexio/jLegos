package com.velexio.jlegos.util;


import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;

/**
 * Enum to emulate options for performing file copy
 */
public enum FileCopyOption {
    /**
     * Move in atomic fashion
     */
    ATOMIC_MOVE,
    /**
     * Include all file attributes with the copy
     */
    INCLUDE_ATTRIBUTES,
    /**
     * Overwrite existing file
     */
    REPLACE_EXISTING;

    private CopyOption nioEquiv;

    static {
        ATOMIC_MOVE.nioEquiv = StandardCopyOption.ATOMIC_MOVE;
        INCLUDE_ATTRIBUTES.nioEquiv = StandardCopyOption.COPY_ATTRIBUTES;
        REPLACE_EXISTING.nioEquiv = StandardCopyOption.REPLACE_EXISTING;
    }

    /**
     * Gets the File.nio equivalent
     *
     * @return Copy Option of equivalent option
     */
    public CopyOption getNioEquiv() {
        return nioEquiv;
    }
}
