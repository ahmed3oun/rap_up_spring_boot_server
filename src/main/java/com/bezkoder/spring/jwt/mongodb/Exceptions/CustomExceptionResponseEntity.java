package com.bezkoder.spring.jwt.mongodb.Exceptions;

import java.util.Date;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//@RestControllerAdvice
@ControllerAdvice
@RestController
public class CustomExceptionResponseEntity extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleAllException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ExceptionResponse> handleUserNotFoundException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ExceptionResponse> handleRoleNotFoundException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FileStreamNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public final ResponseEntity<ExceptionResponse> handleFileStreamNotFoundException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<Object>(
                new ExceptionResponse(new Date(), ex.getMessage(), ex.getBindingResult().toString()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenRefreshException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionResponse> handleTokenRefreshException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ForbiddenMimeTypeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ExceptionResponse> handleForbiddenMimeTypeException(Exception ex, WebRequest request) {
        return new ResponseEntity<ExceptionResponse>(
                new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false)),
                HttpStatus.FORBIDDEN);
    }

}