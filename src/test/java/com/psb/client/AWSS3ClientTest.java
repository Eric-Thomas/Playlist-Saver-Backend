package com.psb.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.psb.constants.Constants;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.model.repository.S3Response;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@ExtendWith(MockitoExtension.class)
class AWSS3ClientTest {

	@Mock
	private S3Client s3;
	@InjectMocks
	private AWSS3Client s3Client;

	@Test
	void testSaveDataSuccess() throws AWSS3ClientException {
		PutObjectResponse awsResponse = PutObjectResponse.builder().eTag(Constants.S3_ETAG).build();
		when(s3.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class))).thenReturn(awsResponse);
		S3Response expectedResponse = new S3Response();
		expectedResponse.setResult(Constants.TEST_S3_RESPONSE);
		expectedResponse.setSuccess(true);
		expectedResponse.setKilobytes(Constants.TEST_S3_RESPONSE_KB);
		S3Response actualResponse = s3Client.saveData(new byte[Constants.TEST_S3_RESPONSE_KB * 1024], "objectKey");
		assertEquals(expectedResponse, actualResponse);
	}

	@Test
	void testSaveDataException() {
		when(s3.putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class)))
				.thenThrow(new RuntimeException());
		assertThrows(AWSS3ClientException.class, () -> {
			s3Client.saveData(new byte[Constants.TEST_S3_RESPONSE_KB * 1024], "objectKey");
		});
	}

	@Test
	void testGetData() throws AWSS3ClientException, AWSS3ClientNotFoundException {
		ResponseBytes<GetObjectResponse> resp = null;
		when(s3.getObjectAsBytes(Mockito.any(GetObjectRequest.class))).thenReturn(resp);
		ResponseBytes<GetObjectResponse> excpectedResp = resp;
		ResponseBytes<GetObjectResponse> actualResp = s3Client.getData("objectKey");
		assertEquals(excpectedResp, actualResp);
	}

	@Test
	void testGetDataException() {
		when(s3.getObjectAsBytes(Mockito.any(GetObjectRequest.class))).thenThrow(new RuntimeException());
		assertThrows(AWSS3ClientException.class, () -> {
			s3Client.getData("objectKey");
		});
	}

}
