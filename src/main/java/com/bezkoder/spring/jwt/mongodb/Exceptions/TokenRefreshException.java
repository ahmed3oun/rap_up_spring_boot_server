package com.bezkoder.spring.jwt.mongodb.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TokenRefreshException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String message, String token) {
        super(String.format("Failed for { [%s] } :: [%s]", token, message));
    }

}