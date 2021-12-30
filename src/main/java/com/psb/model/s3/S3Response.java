package com.psb.model.s3;

import lombok.Data;

@Data
public class S3Response {

	private int bytes;
	private String eTag;
	private boolean success;
	private String bucket;
	private String objectKey;

}
