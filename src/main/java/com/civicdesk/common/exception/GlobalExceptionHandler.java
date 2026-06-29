package com.civicdesk.common.exception;

import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.civicdesk.common.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpired(TokenExpiredException e) {
        return ResponseEntity.status(401).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse> handleForbidden(ForbiddenException e) {
        return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403).body(ApiResponse.error("Access denied"));
    }

    @ExceptionHandler(PasswordNotSetException.class)
    public ResponseEntity<ApiResponse> handlePasswordNotSet(PasswordNotSetException e) {
        return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse> handleBadRequest(BadRequestException e) {
        return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AccountSuspendedException.class)
    public ResponseEntity<ApiResponse> handleSuspended(AccountSuspendedException e) {
        return ResponseEntity.status(423).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ApiResponse> handleInactive(AccountInactiveException e) {
        return ResponseEntity.status(403).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse> handleDuplicate(DuplicateEmailException e) {
        return ResponseEntity.status(409).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(ApiResponse.error(msg));
    }
}
