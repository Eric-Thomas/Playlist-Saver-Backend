package com.psb.model.spotify;

import java.util.List;

import com.psb.model.repository.Playlist;

import lombok.Data;

@Data
public class SpotifyUser {
	
	private String username;
	private String oauthToken;
	private List<Playlist> playlists;

}
