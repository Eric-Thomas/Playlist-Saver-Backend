package com.psb.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.psb.client.SpotifyClient;
import com.psb.constants.Constants;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.testUtil.RepositoryUtil;
import com.psb.testUtil.SpotifyUtil;
import com.psb.util.SpotifyResponseConverter;

@WebMvcTest(controllers = {SpotifyController.class})
public class SpotifyControllerTest {
	
	@LocalServerPort
    private static int port;
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private SpotifyResponseConverter spotifyResponseConverter;
	@MockBean
	private SpotifyClient spotifyClient;
	private SpotifyUtil spotifyUtil = new SpotifyUtil();
	private RepositoryUtil repositoryUtil = new RepositoryUtil();
	
	private static final String OAUTH = "oauthToken";
	private static final String ERROR_MESSAGE = "Test error message";
	
	 @BeforeEach
	    public void initialize(){
	        spotifyUtil.setMockServerUrl(String.format("http://localhost:%s", 
	                        port));
	    }
	
	@Test
	public void testDefaultPlaylist() throws Exception {
		SpotifyPlaylists testPlaylists = spotifyUtil.createTestPlaylists();
		when(spotifyClient.getPlaylists(Mockito.any(String.class))).thenReturn(testPlaylists);
		when(spotifyClient.getPlaylistTracks(Mockito.any(String.class), 
				Mockito.any(SpotifyPlaylist.class))).thenReturn(
						spotifyUtil.createTestTracks());
		when(spotifyResponseConverter.convertPlaylist(Mockito.any(SpotifyPlaylist.class), 
				Mockito.any(SpotifyTracks.class))).thenReturn(
				repositoryUtil.createTestRepositoryPlaylist());
		
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(Constants.TEST_PLAYLIST_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_ARTIST_NAME)))
				.andExpect(content().string(containsString(Constants.TEST_SONG_NAME)));		
	}
	
	@Test
	public void testInvalidOauthToken() throws Exception{
		when(spotifyClient.getPlaylists(Mockito.any(String.class)))
		.thenThrow(new SpotifyClientUnauthorizedException(ERROR_MESSAGE));
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists")
				.contentType(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH))
				.andExpect(status().isUnauthorized())
				.andExpect(content().string(containsString(ERROR_MESSAGE)));		
	}
	
	@Test
	public void testNoContentTypeHeader() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists"))
				.andExpect(status().isBadRequest());		
	}
	
	@Test
	public void testInvalidBody() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());		
	}
	
	@Test
	public void testGetPlaylistsError() throws Exception {
		when(spotifyClient.getPlaylists(Mockito.anyString()))
		.thenThrow(new SpotifyClientException(ERROR_MESSAGE));
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists")
				.accept(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}
	
	@Test
	public void testGetTracksError() throws Exception {
		when(spotifyClient.getPlaylists(Mockito.anyString()))
		.thenReturn(spotifyUtil.createTestPlaylists());
		when(spotifyClient.getPlaylistTracks(Mockito.anyString(), Mockito.any(SpotifyPlaylist.class)))
		.thenThrow(new SpotifyClientException(ERROR_MESSAGE));
		this.mockMvc.perform(MockMvcRequestBuilders
				.get("/spotify/playlists")
				.accept(MediaType.APPLICATION_JSON)
				.header("OAUTHToken", OAUTH)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(content().string(containsString(ERROR_MESSAGE)));
	}
	

}
