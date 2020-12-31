package com.psb.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.psb.exception.AWSS3ClientException;

import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler 
extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { WebClientResponseException.Unauthorized.class })
	protected ResponseEntity<Object> handleWebClientConflict(
			WebClientResponseException.Unauthorized ex, WebRequest request) {
		String bodyOfResponse = "Invalid Spotify oauth token";
		return handleExceptionInternal(ex, bodyOfResponse, 
				new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(value = { AWSS3ClientException.class })
	protected ResponseEntity<Object> handleS3Exception(
			AWSS3ClientException ex, WebRequest request){
		String bodyOfResponse = "Error calling S3. Try again later";
		return handleExceptionInternal(ex, bodyOfResponse,
				new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
	}
}
