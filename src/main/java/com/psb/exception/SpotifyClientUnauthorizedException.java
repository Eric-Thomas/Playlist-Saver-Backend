package com.psb.exception;

public class SpotifyClientUnauthorizedException extends Exception {

	private static final long serialVersionUID = 5661341433067159365L;
	
	private String errorMessage;

	public SpotifyClientUnauthorizedException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "Error: " + this.errorMessage;
	}
	
	

}
