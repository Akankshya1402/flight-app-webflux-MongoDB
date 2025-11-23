package com.flightapp.webfluxmongo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex,
                                                        ServerWebExchange exchange) {

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex,
                                                          ServerWebExchange exchange) {

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                       ServerWebExchange exchange) {

        ErrorResponse body = new ErrorResponse(
                Instant.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}



