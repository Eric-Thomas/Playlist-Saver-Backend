package com.psb.client;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.testUtils.SpotifyUtil;

import okhttp3.mockwebserver.MockWebServer;

class SpotifyClientTest {

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
		String baseUrl = String.format("http://localhost:%s", mockSpotifyServer.getPort());
		WebClient client = WebClient.create(baseUrl);
		spotifyClient = new SpotifyClient(client);
		spotifyUtil.setMockServerUrl(baseUrl);
		// Sets playlists url since it is a value drawn from properties file in the
		// class under test
		ReflectionTestUtils.setField(spotifyClient, "basePlaylistsUrl", "/");
		ReflectionTestUtils.setField(spotifyClient, "userInfoUrl", "/");
	}

	@Test
	void testGetPlaylistsNoPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		SpotifyPlaylists testPlaylists = spotifyUtil.createTestPlaylists();
		spotifyUtil.addMockPlaylistsResponse(testPlaylists, mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertEquals(testPlaylists, clientPlaylists);
	}

	@Test
	void testGetPlaylistsWithPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
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
	void testGetPlaylistsNull() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		spotifyUtil.addEmptyBodyResponse(mockSpotifyServer);
		SpotifyPlaylists clientPlaylists = spotifyClient.getPlaylists("oauthToken");
		assertTrue(clientPlaylists.getPlaylists().isEmpty());
	}

	@Test
	void testGetPlaylistsUnauthorized() {
		spotifyUtil.addUnauthorizedResponse(mockSpotifyServer);
		assertThrows(SpotifyClientUnauthorizedException.class, () -> {
			spotifyClient.getPlaylists("oauthToken");
		});
	}

	@Test
	void testGetPlaylists5xxError() {
		spotifyUtil.add5xxResponse(mockSpotifyServer);
		assertThrows(SpotifyClientException.class, () -> {
			spotifyClient.getPlaylists("oauthToken");
		});
	}

	@Test
	void testGetPlaylistTracksNoPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		SpotifyPlaylist testPlaylist = spotifyUtil.createTestPlaylist();
		SpotifyTracks testTracks = spotifyUtil.createTestTracks();
		spotifyUtil.addMockTracksResponse(testTracks, mockSpotifyServer);
		SpotifyTracks clientTracks = spotifyClient.getPlaylistTracks("oauthToken", testPlaylist);
		assertEquals(testTracks, clientTracks);
	}

	@Test
	void testGetPlaylistsTracksWithPagination() throws SpotifyClientException, SpotifyClientUnauthorizedException {
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

	@Test
	void testGetPlaylistTracksNull() throws SpotifyClientException, SpotifyClientUnauthorizedException {
		spotifyUtil.addEmptyBodyResponse(mockSpotifyServer);
		SpotifyPlaylist playlist = spotifyUtil.createTestPlaylist();
		SpotifyTracks clientTracks = spotifyClient.getPlaylistTracks("oauthToken", playlist);
		assertTrue(clientTracks.getTracks().isEmpty());
	}

	@Test
	void testGetPlaylistTracksUnauthorized() {
		// Add 4 unauthorized responses bc there is retry logic in the client
		for (int i = 0; i < 4; i++) {
			spotifyUtil.addUnauthorizedResponse(mockSpotifyServer);
		}
		assertThrows(SpotifyClientUnauthorizedException.class, () -> {
			SpotifyPlaylist playlist = spotifyUtil.createTestPlaylist();
			spotifyClient.getPlaylistTracks("oauthToken", playlist);
		});
	}

	@Test
	void testGetPlaylistTracks5xxError() {
		// Add 4 5xx responses bc there is retry logic in the client
		for (int i = 0; i < 4; i++) {
			spotifyUtil.add5xxResponse(mockSpotifyServer);
		}
		assertThrows(SpotifyClientException.class, () -> {
			SpotifyPlaylist playlist = spotifyUtil.createTestPlaylist();
			spotifyClient.getPlaylistTracks("oauthToken", playlist);
		});
	}
	
	@Test
	void testGetUser() throws SpotifyClientUnauthorizedException, SpotifyClientException {
		SpotifyUser testUser = spotifyUtil.createTestUser();
		spotifyUtil.addMockUserResponse(testUser, mockSpotifyServer);
		assertEquals(testUser, spotifyClient.getUser("oauth"));
	}
	
	@Test
	void testGetUsernameUnauthorized() {
		spotifyUtil.addUnauthorizedResponse(mockSpotifyServer);
		assertThrows(SpotifyClientUnauthorizedException.class, () -> {
			spotifyClient.getUser("oauthToken");
		});
	}
	
	@Test
	void testGetUsername5xxError() {
		spotifyUtil.add5xxResponse(mockSpotifyServer);
		assertThrows(SpotifyClientException.class, () -> {
			spotifyClient.getUser("oauthToken");
		});
	}

}
