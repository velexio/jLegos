package com.velexio.jlegos.util;


import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;

/**
 * Enum to emulate options for performing file copy
 */
public enum FileCopyOption {
    ATOMIC_MOVE,
    INCLUDE_ATTRIBUTES,
    REPLACE_EXISTING;

    private CopyOption nioEquiv;

    static {
        ATOMIC_MOVE.nioEquiv = StandardCopyOption.ATOMIC_MOVE;
        INCLUDE_ATTRIBUTES.nioEquiv = StandardCopyOption.COPY_ATTRIBUTES;
        REPLACE_EXISTING.nioEquiv = StandardCopyOption.REPLACE_EXISTING;
    }

    public CopyOption getNioEquiv() {
        return nioEquiv;
    }
}
