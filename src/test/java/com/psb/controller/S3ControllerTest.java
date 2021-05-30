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

import com.psb.client.AWSS3Client;
import com.psb.exception.AWSS3ClientException;

@WebMvcTest(controllers = { S3Controller.class })
class S3ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AWSS3Client s3Client;

	private static final String ERROR_MESSAGE = "Test error message";

	@Test
	void testLoadError() throws Exception {
		when(s3Client.getData(Mockito.anyString())).thenThrow(new AWSS3ClientException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get("/s3/load").accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON).param("id", "test"))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString("Error calling S3. Try again later")))
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

}
