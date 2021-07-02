package com.psb.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.model.s3.S3Response;
import com.psb.util.Compresser;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

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
			response.setResult(s3Response.eTag()); // eTag is AWS's object hash, i.e. ideally unique ID
			response.setSuccess(true);
			response.setKilobytes(data.length / 1024);
			return response;
		} catch (Exception e) {
			throw new AWSS3ClientException("Error putting object to s3\n" + e.getMessage());
		}
	}

	public ResponseBytes<GetObjectResponse> getPlaylist(String objectKey)
			throws AWSS3ClientException, AWSS3ClientNotFoundException {
		try {
			// LMAO java doesn't have import aliasing so one RequestBody must use the fully
			// qualified name
			GetObjectRequest s3Request = GetObjectRequest.builder().bucket(bucketName).key(objectKey).build();
			return s3.getObjectAsBytes(s3Request);
		} catch (NoSuchKeyException e) {
			throw new AWSS3ClientNotFoundException(
					"Error getting object from s3: Ojbect key: " + objectKey + " does not exist");
		} catch (Exception e) {
			throw new AWSS3ClientException("Error getting object from s3\n" + e.getMessage());
		}
	}

	public List<Object> getPlaylists(String userID) throws AWSS3ClientException, AWSS3ClientNotFoundException {

		List<Object> o = new ArrayList<>();

		String prefix = userID + delimiter;
		ListObjectsRequest listObjects = ListObjectsRequest.builder().bucket(bucketName).prefix(prefix).build();

		ListObjectsResponse res = s3.listObjects(listObjects);
		List<S3Object> objects = res.contents();
		
		if (objects.isEmpty()) {
			throw new AWSS3ClientNotFoundException(
					"Error getting object from s3: user: " + userID + " does not exist");
		}

		for (ListIterator<S3Object> iterVals = objects.listIterator(); iterVals.hasNext();) {
			S3Object s3Object = iterVals.next();
			ResponseBytes<GetObjectResponse> objectBytes = getPlaylist(s3Object.key());
			Object object = SerializationUtils.deserialize(Compresser.decompress(objectBytes.asByteArray()));
			o.add(object);
		}

		return o;
	}

	public Map<String, String> getAllUsers() throws AWSS3ClientException {
		ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).delimiter(delimiter)
				.build();
		try {
			ListObjectsResponse objects = s3.listObjects(listObjectsRequest);
			List<CommonPrefix> prefixes = objects.commonPrefixes();
			return addUsers(prefixes);

		} catch (Exception e) {
			throw new AWSS3ClientException("Error getting object from s3\n" + e.getMessage());
		}
	}
	
	private Map<String, String> addUsers(List<CommonPrefix> userIDs){
		Map<String, String> users = new HashMap<>();
		for (CommonPrefix userID : userIDs) {
			String idPrefix = userID.prefix();
			ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).delimiter(delimiter).prefix(idPrefix)
					.build();
			ListObjectsResponse objects = s3.listObjects(listObjectsRequest);
			String displayName = getDisplayName(objects.commonPrefixes().get(0).prefix());
			String id = idPrefix.substring(0, idPrefix.indexOf(delimiter));
			users.put(id, displayName);
		}
		return users;
	}
	
	private String getDisplayName(String fullPrefix) {
		return fullPrefix.substring(fullPrefix.indexOf(delimiter)+1, fullPrefix.length()-1);
	}

}
