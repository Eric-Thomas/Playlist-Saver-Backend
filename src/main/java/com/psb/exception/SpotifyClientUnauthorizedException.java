package com.psb.exception;

public class SpotifyClientUnauthorizedException extends Exception {

	private static final long serialVersionUID = 5661341433067159365L;

	public SpotifyClientUnauthorizedException(String errorMessage) {
		super(errorMessage);
	}

}
