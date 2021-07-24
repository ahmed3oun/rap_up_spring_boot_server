package com.bezkoder.spring.jwt.mongodb.Exceptions;

public class RoleNotFoundException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RoleNotFoundException(String message) {
        super(message);
    }

}