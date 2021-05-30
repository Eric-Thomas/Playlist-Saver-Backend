package com.psb.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { SpotifyClientUnauthorizedException.class })
	protected ResponseEntity<Object> handleWebClientConflict(SpotifyClientUnauthorizedException ex,
			WebRequest request) {
		String bodyOfResponse = "Error calling spotify api. " + ex.toString();
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}

	@ExceptionHandler(value = { AWSS3ClientException.class })
	protected ResponseEntity<Object> handleS3Exception(AWSS3ClientException ex, WebRequest request) {
		String bodyOfResponse = "Error calling S3. Try again later. " + ex.toString();
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
	}
	
	@ExceptionHandler(value = { AWSS3ClientNotFoundException.class })
	protected ResponseEntity<Object> handleS3Exception(AWSS3ClientNotFoundException ex, WebRequest request) {
		String bodyOfResponse = "Error calling S3 404 Not Found. " + ex.toString();
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(value = { SpotifyClientException.class })
	protected ResponseEntity<Object> handleSpotifyException(SpotifyClientException ex, WebRequest request) {
		String bodyOfResponse = "Error calling spotify api. " + ex.toString();
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.SERVICE_UNAVAILABLE, request);
	}
}
