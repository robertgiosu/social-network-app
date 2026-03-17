package com.ubb.exceptions;

public class DuplicateEntityException extends RuntimeException{

    /**
     * Constructor for duplicate entity exception
     * @param message The error message to be passed to the exception
     */
    public DuplicateEntityException(String message) {
        super(message);
    }
}
