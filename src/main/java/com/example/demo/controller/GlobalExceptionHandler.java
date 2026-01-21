package com.example.demo.controller;

import com.example.demo.exception.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String logName = "[EXCEPTION_LOG]";

    public void logException(Exception e, WebRequest request) {
        String message = logName.concat(": ").concat(request.getDescription(false));
        log.error(message, e);
    }

    @ExceptionHandler
    public ResponseEntity handleException(Exception e, WebRequest request) {
        logException(e, request);
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity handleWalletNotFoundException(WalletNotFoundException e, WebRequest request) {
        logException(e, request);
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

}
