package com.psb.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.client.AWSS3Client;
import com.psb.exception.AWSS3ClientException;
import com.psb.model.spotify.SpotifyUser;
import com.psb.testUtil.SpotifyUtil;

@WebMvcTest(controllers = {S3Controller.class})
public class S3ControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private AWSS3Client s3Client;
	private ObjectMapper mapper = new ObjectMapper();
	private SpotifyUtil spotifyUtil = new SpotifyUtil();
	
	private static final String OAUTH = "oauthToken";
	
	@Test
	public void testS3GetObjectError() throws Exception {
		when(s3Client.getData(Mockito.anyString()))
		.thenThrow(new AWSS3ClientException("test"));
		SpotifyUser user = spotifyUtil.createTestUser();
		String body = mapper.writeValueAsString(user);
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/s3/load")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH)
				.content(body))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString("Error calling S3. Try again later")));
	}
	
	@Test
	public void testS3PutObjectError() throws Exception {
		when(s3Client.saveData(Mockito.any(byte[].class), Mockito.anyString()))
		.thenThrow(new AWSS3ClientException("test"));
		SpotifyUser user = spotifyUtil.createTestUser();
		String body = mapper.writeValueAsString(user);
		this.mockMvc.perform(MockMvcRequestBuilders
				.put("/s3/save")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH)
				.content(body))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString("Error calling S3. Try again later")));
		
		
	}

}
