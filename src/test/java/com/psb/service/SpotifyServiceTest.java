package com.psb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Tracks;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class SpotifyServiceTest {
	
	private static MockWebServer mockSpotifyServer;
	private SpotifyService spotifyService;
	private static ObjectMapper objectMapper = new ObjectMapper();
	 
    @BeforeAll
    static void setUp() throws IOException {
        mockSpotifyServer = new MockWebServer();
        mockSpotifyServer.start();
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
	    Playlists testProfilePlaylists = new Playlists();
	    testProfilePlaylists.setHref("testHref");
	    Playlist testProfilePlaylist = new Playlist();
	    testProfilePlaylist.setHref(String.format("http://localhost:%s", 
	            mockSpotifyServer.getPort()));
	    testProfilePlaylist.setName("Test");
	    Tracks testProfilePlaylistTracks = new Tracks();
	    testProfilePlaylistTracks.setHref(String.format("http://localhost:%s", 
	            mockSpotifyServer.getPort()));
	    testProfilePlaylist.setTracks(testProfilePlaylistTracks);
	    List<Playlist> testItems = new ArrayList<>();
	    testItems.add(testProfilePlaylist);
	    testProfilePlaylists.setPlaylists(testItems);
	    addPlaylistsResponse(testProfilePlaylists);
	    addTracksResponse(testProfilePlaylist.getTracks());
		Playlists servicePlaylists = spotifyService.getPlaylists("");
		assertEquals(testProfilePlaylists, servicePlaylists);
	}
	
	private void addPlaylistsResponse(Playlists playlists) {
	    try {
			mockSpotifyServer.enqueue(new MockResponse()
					.setBody(objectMapper.writeValueAsString(playlists))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	private void addTracksResponse(Tracks tracks) {
	    try {
			mockSpotifyServer.enqueue(new MockResponse()
					.setBody(objectMapper.writeValueAsString(tracks))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	

}
