package com.psb.model.repository;

import lombok.Data;

@Data
public class S3Response {
	
	private int kilobytes;
	private String result;
	private boolean success;

}
