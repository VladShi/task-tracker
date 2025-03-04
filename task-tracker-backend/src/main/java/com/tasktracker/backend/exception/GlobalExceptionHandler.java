package com.tasktracker.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400
                .body(new ErrorMessageResponse(errorMessage));
    }

    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<ErrorMessageResponse> handleUsernameAlreadyTakenException(
            UsernameAlreadyTakenException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)  // 409
                .body(new ErrorMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialsException(BadCredentialsException ignoredEx) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)  // 401
                .body(new ErrorMessageResponse("Invalid login or password"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> UnexpectedException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageResponse("Internal server error"));
    }
}
