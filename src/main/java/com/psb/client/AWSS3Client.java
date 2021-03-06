package com.psb.client;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.psb.exception.AWSS3ClientException;
import com.psb.model.repository.S3Response;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Component
public class AWSS3Client {

	@Value("${aws.bucket.name}")
	private String bucketName;
	private S3Client s3;
	private Logger logger = Logger.getLogger(AWSS3Client.class.getName());

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
			response.setResult(s3Response.eTag()); // eTag is AWS's object hash, i.e. ideally unique ID
			response.setSuccess(true);
			response.setKilobytes((int) data.length / 1024);
			logger.info("Data size: " + response.getKilobytes() + "kB");
			logger.info("Tag information: " + response.getResult());
			return response;
		} catch (Exception e) {
			throw new AWSS3ClientException("Error putting object to s3\n" + e.getMessage());
		}
	}

	public ResponseBytes<GetObjectResponse> getData(String objectKey) throws AWSS3ClientException {
		try {
			// LMAO java doesn't have import aliasing so one RequestBody must use the fully
			// qualified name
			GetObjectRequest s3Request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
			return s3.getObjectAsBytes(s3Request);
		} catch (Exception e) {
			throw new AWSS3ClientException("Error getting object from s3\n" + e.getMessage());
		}
	}

}
