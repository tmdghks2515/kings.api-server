package com.kings.web.infra.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handle(ResponseStatusException exception) {
        var statusCode = exception.getStatusCode();
        var message = exception.getReason();

        return ResponseEntity
                .status(statusCode)
                .body(new ApiErrorResponse(statusCode.value(), message));
    }
}
