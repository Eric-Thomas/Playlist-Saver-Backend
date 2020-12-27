package com.psb.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
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
	void testGetPlaylistsNoPagination() {
		SpotifyPlaylists testPlaylists = spotifyUtil.createTestPlaylists();
		spotifyUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, clientPlaylists);
	}
	
	@Test
	void testGetPlaylistsWithPagination() {
		List<SpotifyPlaylists> testPlaylistsList = spotifyUtil.createTestPlaylistsWithPagination();
		SpotifyPlaylists testPlaylists = combinePlaylistsList(testPlaylistsList);
		spotifyUtil.addMockPlaylistsPaginationResponses(testPlaylistsList, mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, clientPlaylists);
		assertEquals(testPlaylists.getPlaylists().size(), clientPlaylists.getPlaylists().size());
	}
	
	private SpotifyPlaylists combinePlaylistsList(List<SpotifyPlaylists> list) {
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		List<SpotifyPlaylist> playlistList = new ArrayList<>();
		for (SpotifyPlaylists playlists : list) {
			playlistList.addAll(playlists.getPlaylists());
		}
		spotifyPlaylists.setPlaylists(playlistList);
		return spotifyPlaylists;
	}
	
	@Test
	void testGetPlaylistTracksNoPagination() {
		SpotifyPlaylist testPlaylist = spotifyUtil.createTestPlaylist();
		SpotifyTracks testTracks = spotifyUtil.createTestTracks();
		spotifyUtil.addMockTracksResponse(testTracks, mockSpotifyServer);
		SpotifyTracks clientTracks = spotifyClient.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, clientTracks);
	}
	
	@Test
	void testGetPlaylistsTracksWithPagination() {
		SpotifyPlaylist testPlaylist = spotifyUtil.createTestPlaylist();
		List<SpotifyTracks> testTracksList = spotifyUtil.createTestTracksWithPagination();
		SpotifyTracks testTracks = combineTracksList(testTracksList);
		spotifyUtil.addMockTracksPaginationResponses(testTracksList, mockSpotifyServer);
		SpotifyTracks clientTracks = spotifyClient.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, clientTracks);
		assertEquals(testTracks.getTracks().size(), clientTracks.getTracks().size());	
	}
	
	private SpotifyTracks combineTracksList(List<SpotifyTracks> list) {
		SpotifyTracks spotifyTracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		for (SpotifyTracks tracks : list) {
			tracksList.addAll(tracks.getTracks());
		}
		spotifyTracks.setTracks(tracksList);
		return spotifyTracks;
	}
	

}
