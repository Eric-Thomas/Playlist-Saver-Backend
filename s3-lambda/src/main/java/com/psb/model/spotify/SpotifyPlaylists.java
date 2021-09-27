package com.psb.model.spotify;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyPlaylists implements Serializable {

	private static final long serialVersionUID = -8158609480803014637L;
	
	private String next;
	@JsonProperty("items")
	private List<SpotifyPlaylist> playlists;

}
