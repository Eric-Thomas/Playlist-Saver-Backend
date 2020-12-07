package com.psb.web;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.model.SpotifyUser;
import com.psb.service.SpotifyService;
import com.psb.util.ResponseUtil;

@WebMvcTest
public class WebLayerTest {
	
	@LocalServerPort
    private static int port;
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SpotifyService service;
	private static ResponseUtil responseUtil;
	
	 @BeforeAll
	    public static void setUp() throws IOException {
	        responseUtil = new ResponseUtil(
	        		String.format("http://localhost:%s", 
	                        port));
	    }
	
	@Test
	public void TestDefaultPlaylist() throws Exception {
		SpotifyUser user = new SpotifyUser();
		user.setOauthToken("oauthToken");
		user.setUsername("Eric");
		String requestBody = new ObjectMapper().writeValueAsString(user);
		when(service.getPlaylists(Mockito.any(String.class))).thenReturn(responseUtil.createTestPlaylists());
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/spotify/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Test Playlist1")));
				
	}
	
	@Test
	public void TestInvalidOauthToken() throws Exception {
		SpotifyUser user = new SpotifyUser();
		user.setOauthToken("Invalid oauthToken");
		user.setUsername("Eric");
		String requestBody = new ObjectMapper().writeValueAsString(user);
		when(service.getPlaylists(Mockito.any(String.class)))
		.thenThrow(WebClientResponseException.Unauthorized.class);
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/spotify/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andDo(print())
				.andExpect(status().isUnauthorized())
				.andExpect(content().string(containsString("Invalid Spotify oauth token")));
				
	}
	
	@Test
	public void TestNoContentTypeHeader() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/spotify/playlists"))
				.andDo(print())
				.andExpect(status().isUnsupportedMediaType());
				
	}
	
	@Test
	public void TestInvalidBody() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.post("/spotify/playlists")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());
				
	}

}
