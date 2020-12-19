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

	public SpotifyUtil(String mockServerUrl) {
		this.objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.mockServerUrl = mockServerUrl;
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
			server.enqueue(new MockResponse()
					.setBody(this.objectMapper.writeValueAsString(playlists))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void addMockTracksResponse(SpotifyTracks tracks, MockWebServer server) {
		try {
			server.enqueue(new MockResponse()
					.setBody(this.objectMapper.writeValueAsString(tracks))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
