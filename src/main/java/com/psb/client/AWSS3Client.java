package com.psb.client;


import com.psb.exception.AWSS3ClientException;
import com.psb.model.s3.S3Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.annotation.PreDestroy;

@Component
public class AWSS3Client {

	@Value("${aws.bucket.name}")
	private String bucketName;
	private S3Client s3;
	@Value("${s3.path.delimiter}")
	private String delimiter;
	
	private Logger logger = LoggerFactory.getLogger(AWSS3Client.class);
	@Autowired
	public AWSS3Client(S3Client s3) {
		this.s3 = s3;
	}

	@PreDestroy
	public void tearDown() {
		s3.close();
		logger.info("S3 client closed.");
	}

	public S3Response saveData(byte[] data, String objectKey) throws AWSS3ClientException {
		S3Response response = new S3Response();
		try {
			// LMAO java doesn't have import aliasing so one RequestBody must use the fully
			// qualified name
			PutObjectResponse s3Response = s3.putObject(
					PutObjectRequest.builder().bucket(bucketName).key(objectKey).build(),
					software.amazon.awssdk.core.sync.RequestBody.fromBytes(data));
			response.setETag(s3Response.eTag()); // eTag is AWS's object hash, i.e. ideally unique ID
			response.setSuccess(true);
			response.setBytes(data.length);
			response.setBucket(bucketName);
			response.setObjectKey(objectKey);
			return response;
		} catch (Exception e) {
			throw new AWSS3ClientException("Error calling s3. " + e.getMessage());
		}
	}

}
