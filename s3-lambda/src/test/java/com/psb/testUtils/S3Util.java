package com.psb.testUtils;

import java.util.ArrayList;
import java.util.List;

import com.psb.model.s3.S3Playlist;
import com.psb.model.s3.S3User;

public class S3Util {
	
	private SpotifyUtil spotifyUtil = new SpotifyUtil();
	
	private static final int USERS_COUNT = 10;

	public List<S3Playlist> createTestPlaylists() {
		List<S3Playlist> playlists = new ArrayList<>();
		S3Playlist playlist = new S3Playlist(spotifyUtil.createTestPlaylist(), spotifyUtil.createTestTracks());
		playlists.add(playlist);
		return playlists;
	}
	
	public List<S3User> createTestUsers(){
		List<S3User> users = new ArrayList<>();
		for (int i = 0; i < USERS_COUNT; i++) {
			users.add(createTestUser(i));
		}
		return users;
	}
	
	private S3User createTestUser(int suffix) {
		S3User user = new S3User();
		user.setDisplayName("User" + suffix);
		user.setId("ID" + suffix);
		return user;
	}

}
