package com.velexio.jlegos.exceptions;

/**
 * Used to indicate when an BashCommand or RemoteCommand execution fails
 */
public class CommandExecutionException extends Exception {
    /**
     * Default constructor
     *
     * @param message Error Message
     */
    public CommandExecutionException(String message) {
        super(message);
    }
}
