package com.psb.exception;

public class SpotifyClientException extends Exception {

	private static final long serialVersionUID = -179894121030362966L;

	private String errorMessage;

	public SpotifyClientException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "Error: " + this.errorMessage;
	}
}
