package com.example.shaden.exception;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.shaden.exception.custom.DuplicateDataException;
import com.example.shaden.exception.custom.ResourceNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage dataNotFoundHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage genericExceptionHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage duplicateDataHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatusCode(HttpStatus.CONFLICT.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

}
