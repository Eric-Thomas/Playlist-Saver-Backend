package com.psb.exception;

public class AWSS3ClientException extends Exception {

	private static final long serialVersionUID = 276784333461578392L;

	public AWSS3ClientException(String errorMessage) {
		super(errorMessage);
	}

}
