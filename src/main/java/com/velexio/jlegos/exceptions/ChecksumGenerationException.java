package com.velexio.jlegos.exceptions;

/**
 * Will be thrown when there is an error generating a checksum value
 */
public class ChecksumGenerationException extends Exception {
    public ChecksumGenerationException() {
        super("Unable to generate checksum. See log for details");
    }
}
