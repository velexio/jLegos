package com.velexio.jlegos.exceptions;

public class RemoteHostMissingAuthenticationException extends Exception {
    public RemoteHostMissingAuthenticationException() {
        super("Remote host was not provided either a password or is missing keys in order to perform authentication.");
    }
}
