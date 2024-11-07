package com.dimitriskikidis.fuelappserver.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleException(
            ResponseStatusException e,
            HttpServletRequest request
    ) {
        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
    }
}
