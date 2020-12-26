package com.psb.client;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.psb.model.repository.S3Response;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Component
public class AWSS3Client {
	
	@Value("${aws.bucket.name}")
	private String bucketName;
	private Region region = Region.US_EAST_2;
	private S3Client s3;
	
	@PostConstruct
	public void init() {
		 s3 = S3Client.builder().region(region).build();
		 System.out.println("S3 client built.");
	}
	
	@PreDestroy
	public void tearDown() {
		s3.close();
		System.out.println("S3 client closed.");
	}
	
	public S3Response saveData(byte[] data, String objectKey) {
		S3Response response = new S3Response();
        try {
        	// LMAO java doesn't have import aliasing so one RequestBody must use the fully qualified name
            PutObjectResponse s3Response = s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                            software.amazon.awssdk.core.sync.RequestBody.fromBytes(data)); 
            response.setResult(s3Response.eTag()); // eTag is AWS's object hash, i.e. ideally unique ID
            response.setSuccess(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            response.setResult(e.getMessage());
            response.setSuccess(false);
        } 
        
		response.setKilobytes((int) data.length / 1024);
		System.out.println("Data size: " + response.getKilobytes() + "kB");
	    System.out.println("Tag information: " + response.getResult());
        
        return response;	
	}
	
	public ResponseBytes<GetObjectResponse> getData(String objectKey) {
		ResponseBytes<GetObjectResponse> objectBytes = null;
        try {
        	// LMAO java doesn't have import aliasing so one RequestBody must use the fully qualified name
            GetObjectRequest s3Request = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();
            objectBytes = s3.getObjectAsBytes(s3Request);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } 
        
        return objectBytes;
	}

}
