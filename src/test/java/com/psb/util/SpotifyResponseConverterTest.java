package com.psb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.psb.model.repository.Playlist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.testUtil.RepositoryUtil;
import com.psb.testUtil.SpotifyUtil;

public class SpotifyResponseConverterTest {

	private SpotifyUtil spotifyUtil = new SpotifyUtil();
	private RepositoryUtil repositoryUtil = new RepositoryUtil();
	private SpotifyResponseConverter spotifyResponseConverter =
			new SpotifyResponseConverter();
	
	@Test
	public void testConvertPlaylist() {
		SpotifyPlaylist spotifyPlaylist = spotifyUtil.createTestPlaylist();
		SpotifyTracks spotifyTracks = spotifyUtil.createTestTracks();
		Playlist repositoryPlaylist = repositoryUtil.createTestRepositoryPlaylist();
		assertEquals(repositoryPlaylist, 
				spotifyResponseConverter.convertPlaylist(spotifyPlaylist,
						spotifyTracks));
	}
}
