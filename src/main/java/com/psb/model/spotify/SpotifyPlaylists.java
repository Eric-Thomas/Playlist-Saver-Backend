package com.psb.model.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyPlaylists {
	
	private String next;
	@JsonProperty("items")
	private List<SpotifyPlaylist> playlists;
	
}
