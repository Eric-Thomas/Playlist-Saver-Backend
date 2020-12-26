package com.psb.client;

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
import com.psb.testUtil.SpotifyUtil;

import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class SpotifyClientTest {
	
	private static MockWebServer mockSpotifyServer;
	private SpotifyClient spotifyClient;
	private SpotifyUtil spotifyUtil = new SpotifyUtil();

    @BeforeAll
    public static void setUp() throws IOException {
        mockSpotifyServer = new MockWebServer();
        mockSpotifyServer.start();
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
        spotifyClient = new SpotifyClient(client);
        spotifyUtil.setMockServerUrl(baseUrl);
    }
	
	@Test
	void testGetPlaylists() {
		SpotifyPlaylists testPlaylists = spotifyUtil.createTestPlaylists();
		spotifyUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		SpotifyPlaylists servicePlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, servicePlaylists);
	}
	
	@Test
	void testGetPlaylistTracks() {
		SpotifyPlaylist testPlaylist = spotifyUtil.createTestPlaylist();
		SpotifyTracks testTracks = spotifyUtil.createTestTracks();
		spotifyUtil.addMockTracksResponse(testTracks, mockSpotifyServer);
		SpotifyTracks serviceTracks = spotifyClient.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, serviceTracks);
	}
	

}
