package com.psb.model.response;

import com.psb.model.spotify.SpotifyTracks;

import lombok.Data;

@Data
public class PlaylistResponse {
	
	String playlistName;
	SpotifyTracks tracks;

}
