package com.psb.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.model.Album;
import com.psb.model.Artist;
import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Tracks;
import com.psb.model.Track;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
public class ResponseUtil {

	private ObjectMapper objectMapper;
	private String mockServerUrl;

	public ResponseUtil(String mockServerUrl) {
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
		testPlaylist.setName("Test");
		testPlaylist.setTracksUrl(this.mockServerUrl + "/tracks");
		return testPlaylist;
	}

	public Tracks createTestTracks() {
		Tracks testTracks = new Tracks();
		testTracks.setHref(this.mockServerUrl);
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
		testTrack.setName("Runaway");
		testTrack.setUri("Test uri");
		return testTrack;
		
	}
	
	public Album createTestAlbum() {
		Album testAlbum = new Album();
		testAlbum.setName("My Beautiful Dark Twisted Fantasy");
		return testAlbum;
	}

	public Artist createTestArtist() {
		Artist testArtist = new Artist();
		testArtist.setName("Kanye");
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
