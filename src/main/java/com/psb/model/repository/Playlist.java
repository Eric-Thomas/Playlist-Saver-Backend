package com.psb.model.repository;

import com.psb.model.spotify.SpotifyTracks;

import lombok.Data;

@Data
public class Playlist {
	
	String playlistName;
	SpotifyTracks tracks;

}
