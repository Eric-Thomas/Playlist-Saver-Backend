package com.psb.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.constants.Constants;
import com.psb.model.Album;
import com.psb.model.Artist;
import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Track;
import com.psb.model.Tracks;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class SpotifyUtil {

	private ObjectMapper objectMapper;
	private String mockServerUrl;

	public SpotifyUtil(String mockServerUrl) {
		this.objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		this.mockServerUrl = mockServerUrl;
	}


	public Playlists createTestPlaylists() {
		Playlists testPlaylists = new Playlists();
		testPlaylists.setHref(this.mockServerUrl);
		List<Playlist> playlists = new ArrayList<>();
		playlists.add(createTestPlaylist());
		testPlaylists.setPlaylists(playlists);
		return testPlaylists;
	}

	public Playlist createTestPlaylist() {
		Playlist testPlaylist = new Playlist();
		testPlaylist.setHref(this.mockServerUrl);
		testPlaylist.setName(Constants.TEST_PLAYLIST_NAME);
		testPlaylist.setTracksUrl(this.mockServerUrl + Constants.TRACKS_URL);
		return testPlaylist;
	}

	public Tracks createTestTracks() {
		Tracks testTracks = new Tracks();
		List<Track> tracks = new ArrayList<>();
		tracks.add(createTestTrack());
		testTracks.setTracks(tracks);
		return testTracks;
	}
	
	public Track createTestTrack() {
		Track testTrack = new Track();
		testTrack.setAlbum(createTestAlbum());
		List<Artist> artists = new ArrayList<>();
		artists.add(createTestArtist());
		testTrack.setArtists(artists);
		testTrack.setName(Constants.TEST_SONG_NAME);
		testTrack.setUri("Test uri");
		return testTrack;
		
	}
	
	public Album createTestAlbum() {
		Album testAlbum = new Album();
		testAlbum.setName(Constants.TEST_ALBUM_NAME);
		return testAlbum;
	}

	public Artist createTestArtist() {
		Artist testArtist = new Artist();
		testArtist.setName(Constants.TEST_ARTIST_NAME);
		return testArtist;
	}
	
	public void addMockPlaylistsResponse(Playlists playlists, MockWebServer server) {
		try {
			server.enqueue(new MockResponse()
					.setBody(this.objectMapper.writeValueAsString(playlists))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	public void addMockTracksResponse(Tracks tracks, MockWebServer server) {
		try {
			server.enqueue(new MockResponse()
					.setBody(this.objectMapper.writeValueAsString(tracks))
					.addHeader("Content-Type", "application/json"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
