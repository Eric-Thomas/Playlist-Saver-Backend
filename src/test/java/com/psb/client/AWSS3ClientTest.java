package com.psb.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psb.constants.Constants;
import com.psb.exception.AWSS3ClientException;
import com.psb.model.s3.S3Response;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class AWSS3ClientTest {

	@Mock
	private S3Client s3;
	@InjectMocks
	private AWSS3Client s3Client;

	@BeforeEach
	void initialize() {
		// Sets bucket since it is a value drawn from properties file in the
		// class under test
		ReflectionTestUtils.setField(s3Client, "bucketName", Constants.TEST_S3_BUCKET);
	}

	@Test
	void testSaveDataSuccess() throws AWSS3ClientException {
		PutObjectResponse awsResponse = PutObjectResponse.builder().eTag(Constants.TEST_S3_ETAG).build();
		when(s3.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class))).thenReturn(awsResponse);
		S3Response expectedResponse = new S3Response();
		expectedResponse.setETag(Constants.TEST_S3_ETAG);
		expectedResponse.setSuccess(true);
		expectedResponse.setBytes(Constants.TEST_S3_RESPONSE_BYTES);
		expectedResponse.setBucket(Constants.TEST_S3_BUCKET);
		expectedResponse.setObjectKey(Constants.TEST_OBJECT_KEY);
		S3Response actualResponse = s3Client.saveData(new byte[Constants.TEST_S3_RESPONSE_BYTES], Constants.TEST_OBJECT_KEY);
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void testSaveDataException() {

		when(s3.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class)))
				.thenThrow(new RuntimeException());
		assertThrows(AWSS3ClientException.class, () -> {
			s3Client.saveData(new byte[Constants.TEST_S3_RESPONSE_BYTES], Constants.TEST_OBJECT_KEY);
		});
	}

}
