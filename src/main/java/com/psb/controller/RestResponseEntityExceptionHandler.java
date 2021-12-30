package com.psb.controller;

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
	public ResponseEntity<Object> handleUnauthorizedException(SpotifyClientUnauthorizedException e) {
		ExceptionResponse res = new ExceptionResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = { SpotifyClientException.class })
	public ResponseEntity<Object> handleClientException(SpotifyClientException e) {
		ExceptionResponse res = new ExceptionResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
		return new ResponseEntity<>(res, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(value = { AWSS3ClientException.class })
	protected ResponseEntity<Object> handleS3Exception(AWSS3ClientException ex) {
		ExceptionResponse res = new ExceptionResponse("Error calling S3. Try again later. " + ex.getMessage(),
				HttpStatus.SERVICE_UNAVAILABLE);
		return new ResponseEntity<>(res, HttpStatus.SERVICE_UNAVAILABLE);
	}
}
