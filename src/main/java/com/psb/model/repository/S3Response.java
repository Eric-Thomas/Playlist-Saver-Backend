package com.psb.model.repository;

import lombok.Data;

@Data
public class S3Response {
	
	private int count;
	private String result;
	private boolean success;

}
