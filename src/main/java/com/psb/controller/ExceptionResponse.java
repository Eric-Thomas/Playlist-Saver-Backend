package com.psb.controller;

import org.springframework.http.HttpStatus;

public class ExceptionResponse {
	private final String message;
	private final HttpStatus httpStatus;

	public ExceptionResponse(String message, HttpStatus httpStatus) {
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}