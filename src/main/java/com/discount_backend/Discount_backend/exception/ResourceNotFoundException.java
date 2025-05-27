package com.discount_backend.Discount_backend.exception;

import jakarta.persistence.metamodel.SingularAttribute;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s not found with id %d", resourceName, id));
    }

    // if you still need your existing constructor:
    public ResourceNotFoundException(String entityName,
                                     SingularAttribute<?, ?> idAttribute) {
        super(String.format("%s not found for attribute %s",
                entityName, idAttribute.getName()));
    }

    public ResourceNotFoundException(String s, String userRole) {
        super(String.format("%s not found with role %d", s, userRole));
    }
}
