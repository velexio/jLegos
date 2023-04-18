package com.velexio.jlegos.exceptions;

/**
 * Thrown when remote host does not have either password or ssh key set
 */
public class RemoteHostMissingAuthenticationException extends Exception {
    /**
     * Default constructor
     */
    public RemoteHostMissingAuthenticationException() {
        super("Remote host was not provided either a password or is missing keys in order to perform authentication.");
    }
}
