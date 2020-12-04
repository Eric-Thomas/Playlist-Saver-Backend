package com.psb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Tracks;
import com.psb.util.ResponseUtil;

import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class SpotifyServiceTest {
	
	private static MockWebServer mockSpotifyServer;
	private SpotifyService spotifyService;
	private static ResponseUtil responseUtil;

    @BeforeAll
    static void setUp() throws IOException {
        mockSpotifyServer = new MockWebServer();
        mockSpotifyServer.start();
        responseUtil = new ResponseUtil(
        		String.format("http://localhost:%s", 
                        mockSpotifyServer.getPort()));
    }
 
    @AfterAll
    static void tearDown() throws IOException {
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
		Playlists testPlaylists = responseUtil.createTestPlaylists();
		responseUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		Playlists servicePlaylists = spotifyService.getPlaylists("oauthToken");
		assertEquals(testPlaylists, servicePlaylists);
	}
	
	@Test
	void testGetPlaylistTracks() {
		Playlist testPlaylist = responseUtil.createTestPlaylist();
		Tracks testTracks = responseUtil.createTestTracks();
		responseUtil.addMockTracksResponse(testTracks, mockSpotifyServer);
		Tracks serviceTracks = spotifyService.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, serviceTracks);
	}


}
