package com.tasktracker.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorMessageResponse> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.warn("Failed to parse JSON request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400
                .body(new ErrorMessageResponse("Invalid JSON format"));
    }

    @ExceptionHandler(JwtProcessingException.class)
    public ResponseEntity<ErrorMessageResponse> handleJwtProcessingException(JwtProcessingException ex) {
        log.warn("Failed to parse JWT request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST).  // 400
                body(new ErrorMessageResponse("Failed to parse JWT. " + ex.getMessage()));
    }

    @ExceptionHandler(TaskOwnershipException.class)
    public ResponseEntity<ErrorMessageResponse> handleTaskOwnershipException(TaskOwnershipException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)  // 403
                .body(new ErrorMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleTaskNotFoundException(TaskNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)  // 404
                .body(new ErrorMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(TaskDeletingException.class)
    public ResponseEntity<ErrorMessageResponse> handleTaskDeletingException(TaskDeletingException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)  // 404
                .body(new ErrorMessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorMessageResponse> handleNoResourceFound(NoResourceFoundException ignoredEx) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)  // 404
                .body(new ErrorMessageResponse("Resource not found"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorMessageResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String allowHeader = Optional.ofNullable(ex.getSupportedHttpMethods())
                .map(methods -> methods.stream().map(HttpMethod::name).collect(Collectors.joining(", ")))
                .orElse("");
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)  // 405
                .header(HttpHeaders.ALLOW, allowHeader)
                .body(new ErrorMessageResponse("Method not allowed"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
                .body(new ErrorMessageResponse("Internal server error"));
    }
}
