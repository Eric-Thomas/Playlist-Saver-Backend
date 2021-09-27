package com.psb.model.s3;

import java.io.Serializable;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTracks;

import lombok.Data;

@Data
public class S3Playlist implements Serializable {
	
	private static final long serialVersionUID = 4111762650686114692L;
	
	public S3Playlist(SpotifyPlaylist playlist, SpotifyTracks tracks) {
		this.playlist = playlist;
		this.tracks = tracks;
	}
	
	SpotifyPlaylist playlist;
	SpotifyTracks tracks;

}
