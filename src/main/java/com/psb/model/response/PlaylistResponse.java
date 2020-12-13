package com.psb.model.response;

import com.psb.model.spotify.Tracks;

import lombok.Data;

@Data
public class PlaylistResponse {
	
	String playlistName;
	Tracks tracks;

}
