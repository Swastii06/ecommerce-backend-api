package com.incture.ecommerceBackend.Exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

	// 1. Test the Exception Object (Coverage for CustomException)
	@Test
	@DisplayName("Test CustomException getters")
	void testCustomException() {
		CustomException ex = new CustomException(HttpStatus.BAD_REQUEST, "Error Occurred");
		assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
		assertEquals("Error Occurred", ex.getMessage());
	}

	// 2. Test the Handler directly (Coverage for GlobalExceptionHandler)
	@Test
	@DisplayName("Test handleCustomException directly")
	void testHandleCustomExceptionDirectly() {
		// Instantiate the handler like a normal Java class (No MockMvc needed!)
		GlobalExceptionHandler handler = new GlobalExceptionHandler();

		// Create an exception object
		CustomException ex = new CustomException(HttpStatus.NOT_FOUND, "Not Found Error");

		// Call the method directly
		ResponseEntity<String> response = handler.handleCustomException(ex);

		// Assertions
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Not Found Error", response.getBody());
	}
}