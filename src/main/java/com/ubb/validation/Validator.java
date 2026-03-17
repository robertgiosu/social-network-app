package com.ubb.validation;

public interface Validator <T>{
    /**
     * Validate an object.
     * @param obj A generic object to validate
     */
    void validate(T obj);
}
