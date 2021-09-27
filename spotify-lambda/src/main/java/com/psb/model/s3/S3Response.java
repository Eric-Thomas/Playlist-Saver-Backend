package com.psb.model.s3;

import lombok.Data;

@Data
public class S3Response {

	private int kilobytes;
	private String result;
	private boolean success;

}
