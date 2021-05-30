package com.psb.testUtil;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.constants.Constants;
import com.psb.model.spotify.SpotifyAlbum;
import com.psb.model.spotify.SpotifyArtist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class SpotifyUtil {

	private ObjectMapper objectMapper;
	private String mockServerUrl;

	private final int PAGINATION_COUNT = 5;
	private final int UNAUTHORIZED = 401;

	public SpotifyUtil() {
		this.objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	public void setMockServerUrl(String url) {
		this.mockServerUrl = url;
	}

	public List<SpotifyPlaylists> createTestPlaylistsWithPagination() {
		List<SpotifyPlaylists> testPlaylistsList = new ArrayList<>();
		for (int i = 0; i < PAGINATION_COUNT; i++) {
			SpotifyPlaylists testPlaylists = new SpotifyPlaylists();
			List<SpotifyPlaylist> playlists = new ArrayList<>();
			playlists.add(createTestPlaylist());
			testPlaylists.setPlaylists(playlists);
			testPlaylists.setNext(mockServerUrl + Constants.TRACKS_URL);
			testPlaylistsList.add(testPlaylists);
		}
		// Set last playlists next field to null to avoid infinite loop
		testPlaylistsList.get(testPlaylistsList.size() - 1).setNext(null);
		return testPlaylistsList;
	}

	public SpotifyPlaylists createTestPlaylists() {
		SpotifyPlaylists testPlaylists = new SpotifyPlaylists();
		List<SpotifyPlaylist> playlists = new ArrayList<>();
		playlists.add(createTestPlaylist());
		testPlaylists.setPlaylists(playlists);
		return testPlaylists;
	}

	public SpotifyPlaylist createTestPlaylist() {
		SpotifyPlaylist testPlaylist = new SpotifyPlaylist();
		testPlaylist.setName(Constants.TEST_PLAYLIST_NAME);
		testPlaylist.setTracksUrl(this.mockServerUrl + Constants.TRACKS_URL);
		return testPlaylist;
	}

	public List<SpotifyTracks> createTestTracksWithPagination() {
		List<SpotifyTracks> testTracksList = new ArrayList<>();
		for (int i = 0; i < PAGINATION_COUNT; i++) {
			SpotifyTracks testTracks = new SpotifyTracks();
			List<SpotifyTrack> tracks = new ArrayList<>();
			tracks.add(createTestTrack());
			testTracks.setTracks(tracks);
			testTracks.setNext(mockServerUrl + Constants.TRACKS_URL);
			testTracksList.add(testTracks);
		}
		// Set last tracks next field to null to avoid infinite loop
		testTracksList.get(testTracksList.size() - 1).setNext(null);
		return testTracksList;
	}

	public SpotifyTracks createTestTracks() {
		SpotifyTracks testTracks = new SpotifyTracks();
		List<SpotifyTrack> tracks = new ArrayList<>();
		tracks.add(createTestTrack());
		testTracks.setTracks(tracks);
		return testTracks;
	}

	public SpotifyTrack createTestTrack() {
		SpotifyTrack testTrack = new SpotifyTrack();
		testTrack.setAlbum(createTestAlbum());
		List<SpotifyArtist> artists = new ArrayList<>();
		artists.add(createTestArtist());
		testTrack.setArtists(artists);
		testTrack.setName(Constants.TEST_SONG_NAME);
		testTrack.setUri("Test uri");
		return testTrack;

	}

	public SpotifyAlbum createTestAlbum() {
		SpotifyAlbum testAlbum = new SpotifyAlbum();
		testAlbum.setName(Constants.TEST_ALBUM_NAME);
		return testAlbum;
	}

	public SpotifyArtist createTestArtist() {
		SpotifyArtist testArtist = new SpotifyArtist();
		testArtist.setName(Constants.TEST_ARTIST_NAME);
		return testArtist;
	}

	public void addMockPlaylistsResponse(SpotifyPlaylists playlists, MockWebServer server) {
		try {
			server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(playlists))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void addUnauthorizedResponse(MockWebServer server) {
		server.enqueue(new MockResponse().setResponseCode(UNAUTHORIZED));
	}

	public void add5xxResponse(MockWebServer server) {
		server.enqueue(new MockResponse().setResponseCode(503));
	}

	public void addMockPlaylistsPaginationResponses(List<SpotifyPlaylists> playlistsList, MockWebServer server) {
		for (SpotifyPlaylists playlists : playlistsList) {
			try {
				server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(playlists))
						.addHeader("Content-Type", "application/json"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

	public void addEmptyBodyResponse(MockWebServer server) {
		server.enqueue(new MockResponse().addHeader("Content-Type", "application/json"));
	}

	public void addMockTracksResponse(SpotifyTracks tracks, MockWebServer server) {
		try {
			server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(tracks))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void addMockTracksPaginationResponses(List<SpotifyTracks> tracksList, MockWebServer server) {
		for (SpotifyTracks tracks : tracksList) {
			try {
				server.enqueue(new MockResponse().setBody(this.objectMapper.writeValueAsString(tracks))
						.addHeader("Content-Type", "application/json"));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
