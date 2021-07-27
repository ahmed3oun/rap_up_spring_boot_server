package com.bezkoder.spring.jwt.mongodb.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenMimeTypeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ForbiddenMimeTypeException(String message) {
        super(message);
    }

}