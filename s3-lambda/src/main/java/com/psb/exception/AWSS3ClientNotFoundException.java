package com.psb.exception;

public class AWSS3ClientNotFoundException extends Exception{

	private static final long serialVersionUID = 6934161955983268655L;
	private final String errorMessage;

	public AWSS3ClientNotFoundException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String toString() {
		return "Error: " + this.errorMessage;
	}

}
