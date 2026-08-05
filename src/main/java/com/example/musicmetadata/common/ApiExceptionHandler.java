package com.example.musicmetadata.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;
import java.util.LinkedHashMap;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail notFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    ProblemDetail conflict(ConflictException exception) {
        return problem(HttpStatus.CONFLICT, exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail validation(MethodArgumentNotValidException exception) {
        ProblemDetail detail = problem(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed");
        var errors = new LinkedHashMap<String, String>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        detail.setProperty("errors", errors);
        return detail;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    ProblemDetail parameterValidation(HandlerMethodValidationException exception) {
        return problem(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request parameter validation failed");
    }

    private ProblemDetail problem(HttpStatus status, String code, String message) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(status, message);
        detail.setTitle(status.getReasonPhrase());
        detail.setType(URI.create("https://api.example.com/problems/" + code.toLowerCase().replace('_', '-')));
        detail.setProperty("code", code);
        return detail;
    }
}
