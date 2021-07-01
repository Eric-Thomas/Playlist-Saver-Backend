package com.psb.testUtil;

import java.util.ArrayList;
import java.util.List;

import com.psb.constants.Constants;
import com.psb.model.repository.Album;
import com.psb.model.repository.Artist;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Track;

public class RepositoryUtil {

	public Playlist createTestRepositoryPlaylist() {
		Playlist testPlaylist = new Playlist();
		testPlaylist.setPlaylistName(Constants.TEST_PLAYLIST_NAME);
		testPlaylist.setTracks(
				createTestTracks());
		testPlaylist.setId(Constants.TEST_PLAYLIST_ID);
		return testPlaylist;
	}

	public List<Track> createTestTracks() {
		List<Track> testTracks = new ArrayList<>();
		testTracks.add(createTestTrack());
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

}
