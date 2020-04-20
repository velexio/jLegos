package com.velexio.jlegos.util;


import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;

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
