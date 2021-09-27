package com.psb.exception;

public class AWSS3ClientException extends Exception {

	private static final long serialVersionUID = 276784333461578392L;
	
	private final String errorMessage;

	public AWSS3ClientException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "Error: " + this.errorMessage;
	}

}
