package com.incture.ecommerceBackend.Exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

	HttpStatus status;

	public CustomException(HttpStatus status, String msg) {
		super(msg);
		this.status = status; // Stores the HTTP status code in the exception object
	}

	public HttpStatus getStatus() {
		return status;
	}

}
