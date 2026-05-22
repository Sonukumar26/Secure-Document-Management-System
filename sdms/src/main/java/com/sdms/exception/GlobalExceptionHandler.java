package com.sdms.exception;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔐 JWT ERRORS
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwt(
            JwtException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                401,
                "Unauthorized",
                "Invalid or Expired JWT Token",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // ❌ BAD LOGIN
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadLogin(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                401,
                "Unauthorized",
                "Invalid username or password",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    // ⛔ ACCESS DENIED
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ApiError error = new ApiError(
                403,
                "Forbidden",
                "You don't have permission",
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    // 💥 ANY OTHER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(
            Exception ex,
            HttpServletRequest request) {

        ex.printStackTrace();

        ApiError error = new ApiError(
                500,
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
