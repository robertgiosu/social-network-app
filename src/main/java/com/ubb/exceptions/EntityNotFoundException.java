package com.ubb.exceptions;

public class EntityNotFoundException extends RuntimeException{

    /**
     * Constructor for entity not found exception
     * @param message The error message to be passed to the exception
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
