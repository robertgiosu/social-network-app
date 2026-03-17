package com.ubb.exceptions;

public class ValidationException extends RuntimeException{

    /**
     * Constructor for ValidationException
     * @param message The error message to be passed to the exception
     */
    public ValidationException(String message) {
        super(message);
    }
}
