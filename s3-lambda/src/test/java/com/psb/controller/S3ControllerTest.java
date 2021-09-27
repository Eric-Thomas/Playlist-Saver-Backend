package com.psb.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.psb.client.AWSS3Client;
import com.psb.constants.Constants;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.model.s3.S3User;
import com.psb.testUtils.S3Util;

@WebMvcTest(controllers = { S3Controller.class })
class S3ControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AWSS3Client s3Client;

	private S3Util s3Util = new S3Util();

	private static final String S3_LOAD_PLAYLISTS_URL = "/s3/load/users/id/playlists";
	private static final String S3_LOAD_USERS_URL = "/s3/load/users";
	private static final String ERROR_MESSAGE = "Test error message";

	@Test
	void testLoadPlaylists() throws Exception {
		when(s3Client.getPlaylists(Mockito.anyString())).thenReturn(s3Util.createTestPlaylists());
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(S3_LOAD_PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(containsString(Constants.TEST_PLAYLIST_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_ALBUM_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_ARTIST_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_SONG_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_PLAYLIST_IMAGE_URL)));
	}

	@Test
	void testLoadPlaylistsError() throws Exception {
		when(s3Client.getPlaylists(Mockito.anyString())).thenThrow(new AWSS3ClientException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(S3_LOAD_PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString("Error calling S3. Try again later")))
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

	@Test
	void testLoadPlaylistsUserNotFound() throws Exception {
		when(s3Client.getPlaylists(Mockito.anyString())).thenThrow(new AWSS3ClientNotFoundException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(S3_LOAD_PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().string(containsString("Error calling S3 404 Not Found.")))
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

	@Test
	void testLoadUsers() throws Exception {
		List<S3User> testUsers = s3Util.createTestUsers();
		when(s3Client.getAllUsers()).thenReturn(testUsers);
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(S3_LOAD_USERS_URL).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is2xxSuccessful())
				.andExpect(content().string(containsString(testUsers.get(0).getDisplayName())))
				.andExpect(content().string(containsString(testUsers.get(0).getId())));
	}

	@Test
	void testLoadUsersException() throws Exception {
		when(s3Client.getAllUsers()).thenThrow(new AWSS3ClientException(ERROR_MESSAGE));
		this.mockMvc
				.perform(MockMvcRequestBuilders.get(S3_LOAD_USERS_URL).accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString("Error calling S3. Try again later")))
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}

}
