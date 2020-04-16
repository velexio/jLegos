package com.velexio.jlegos.exceptions;

public class ChecksumGenerationException extends Exception {
    public ChecksumGenerationException() {
        super("Unable to generate checksum. See log for details");
    }
}
