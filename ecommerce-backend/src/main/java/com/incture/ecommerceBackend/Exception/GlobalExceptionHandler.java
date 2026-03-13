package com.incture.ecommerceBackend.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This annotation tells Spring to use this class to handle errors across all controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

	// When a ResourceNotFoundException happens, do this:
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> handleResourceNotFoundException(CustomException ex) {
		// Returns a clean 404 NOT FOUND status with our custom message
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

	// A fallback for any other unexpected errors
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGlobalException(Exception ex) {
		return new ResponseEntity<>("An internal error occurred: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}