package com.velexio.jlegos.exceptions;

/**
 * Thrown when unable to initialize connection to remote host
 */
public class RemoteHostInitializationException extends Exception {
    /**
     * Default Constructor
     *
     * @param message Error message
     */
    public RemoteHostInitializationException(String message) {
        super(message);
    }
}
