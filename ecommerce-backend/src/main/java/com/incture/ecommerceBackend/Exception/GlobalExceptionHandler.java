package com.incture.ecommerceBackend.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// This annotation tells Spring to use this class to handle errors across all controllers
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = CustomException.class) // This method will automatically run whenever a CustomException is
														// thrown anywhere in the application.
	public ResponseEntity<String> handleCustomException(CustomException exception) {
		return new ResponseEntity<>(exception.getMessage(), exception.getStatus());
	}
}