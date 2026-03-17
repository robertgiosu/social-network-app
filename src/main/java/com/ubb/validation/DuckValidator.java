package com.ubb.validation;

import com.ubb.domain.Duck;
import com.ubb.exceptions.ValidationException;

import java.util.Objects;

public class DuckValidator implements Validator<Duck>{
    /**
     * Validate a duck, checking all of its attributes.
     * @param d Duck to validate
     * @throws ValidationException if any of the attributes are invalid
     */
    @Override
    public void validate(Duck d) {
        if (d == null) throw new ValidationException("Duck cannot be null");
        if (d.getId() == null || d.getId().isBlank()) throw new ValidationException("Duck id cannot be null or empty");
        if (d.getUserName() == null || d.getUserName().isBlank()) throw new ValidationException("Duck username cannot be null or empty");
        if (d.getSpeed() < 0) throw new ValidationException("Duck speed cannot be negative");
        if (d.getEndurance() < 0) throw new ValidationException("Duck endurance cannot be negative");
    }
}
