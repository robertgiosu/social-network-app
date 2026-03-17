package com.ubb.validation;

import com.ubb.domain.Person;
import com.ubb.exceptions.ValidationException;

public class PersonValidator implements Validator<Person>{
    /**
     * Validate a person, checking all of its attributes.
     * @param p Person to validate
     * @throws ValidationException if any of the attributes are invalid
     */
    @Override
    public void validate(Person p) {
        if (p == null) throw new ValidationException("Person cannot be null");
        if (p.getId() == null || p.getId().isBlank()) throw new ValidationException("Person id cannot be null or empty");
        if (p.getUserName() == null || p.getUserName().isBlank()) throw new ValidationException("Person username cannot be null or empty");
        if (p.getFirstName() == null || p.getFirstName().isBlank()) throw new ValidationException("Person first name cannot be null or empty");
        if (p.getLastName() == null || p.getLastName().isBlank()) throw new ValidationException("Person last name cannot be null or empty");
        if (p.getOccupation() == null || p.getOccupation().isBlank()) throw new ValidationException("Person occupation cannot be null or empty");
    }
}
