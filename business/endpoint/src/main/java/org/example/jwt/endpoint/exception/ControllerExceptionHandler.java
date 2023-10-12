package org.example.jwt.endpoint.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { RuntimeException.class })
    public ResponseEntity<Object> uncaughtException(RuntimeException rte, WebRequest request) {
        return ResponseEntity.internalServerError().body(ResponseEntity.internalServerError().body(rte.getMessage()));
    }
}
