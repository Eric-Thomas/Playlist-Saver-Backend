package com.psb.exception;

public class SpotifyClientException extends Exception {

	private static final long serialVersionUID = -179894121030362966L;

	public SpotifyClientException(String errorMessage) {
		super(errorMessage);
	}
}
