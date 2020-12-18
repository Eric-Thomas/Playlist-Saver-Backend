package com.psb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.SpotifyUtil;

import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class SpotifyServiceTest {
	
	private static MockWebServer mockSpotifyServer;
	private SpotifyService spotifyService;
	private static SpotifyUtil responseUtil;

    @BeforeAll
    public static void setUp() throws IOException {
        mockSpotifyServer = new MockWebServer();
        mockSpotifyServer.start();
        responseUtil = new SpotifyUtil(
        		String.format("http://localhost:%s", 
                        mockSpotifyServer.getPort()));
    }
 
    @AfterAll
    public static void tearDown() throws IOException {
        mockSpotifyServer.shutdown();
    }
    
    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s", 
          mockSpotifyServer.getPort());
        WebClient client = WebClient.create(baseUrl);
        spotifyService = new SpotifyService(client);
    }
	
	@Test
	void testGetPlaylists() {
		SpotifyPlaylists testPlaylists = responseUtil.createTestPlaylists();
		responseUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		SpotifyPlaylists servicePlaylists = spotifyService.getPlaylists("oauthToken");
		assertEquals(testPlaylists, servicePlaylists);
	}
	
	@Test
	void testGetPlaylistTracks() {
		SpotifyPlaylist testPlaylist = responseUtil.createTestPlaylist();
		SpotifyTracks testTracks = responseUtil.createTestTracks();
		responseUtil.addMockTracksResponse(testTracks, mockSpotifyServer);
		SpotifyTracks serviceTracks = spotifyService.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, serviceTracks);
	}
	

}
