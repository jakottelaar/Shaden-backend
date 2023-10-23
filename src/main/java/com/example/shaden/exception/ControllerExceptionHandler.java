package com.example.shaden.exception;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.shaden.exception.custom.DuplicateDataException;
import com.example.shaden.exception.custom.ResourceNotFoundException;
import com.example.shaden.exception.custom.UnauthorizedException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage dataNotFoundHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage genericExceptionHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(DuplicateDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage duplicateDataHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.CONFLICT.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage unauthorizedHandler(Exception e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setTimestamp(new Date());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleValidationException(MethodArgumentNotValidException e) {
        List<String> validationErrors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(new Date());
        error.setMessage("Validation failed");
        error.setErrors(validationErrors.isEmpty() ? null : Optional.of(validationErrors));

        return error;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorMessage handleAuthenticationException(AuthenticationException e) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setTimestamp(new Date());
        error.setMessage("Authentication failed");
        return error;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage error = new ErrorMessage();
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(new Date());
        error.setMessage(ex.getMessage());
        return error;
    }
}
