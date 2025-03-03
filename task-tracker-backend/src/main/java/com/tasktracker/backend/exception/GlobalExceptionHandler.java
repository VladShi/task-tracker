package com.tasktracker.backend.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessageResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getAllErrors().getLast().getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)  // 400
                .body(new ErrorMessageResponse(errorMessage));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessageResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ignoredEx) {
        String errorMessage = "This email is already taken";  // TODO либо проверять причину ошибки либо сделать кастомное исключение
        return ResponseEntity
                .status(HttpStatus.CONFLICT)  // 409
                .body(new ErrorMessageResponse(errorMessage));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialsException(BadCredentialsException ignoredEx) {
        String errorMessage = "Invalid login or password";
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)  // 401
                .body(new ErrorMessageResponse(errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(Exception ex) {
        String errorMessage = "Internal server error";
        ex.printStackTrace();  // TODO Заменить на логирование
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorMessageResponse(errorMessage));
    }
}
