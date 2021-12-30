package com.psb.model.spotify;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyPlaylist implements Serializable {

	private static final long serialVersionUID = -7982164757913098056L;
	
	private String name;
	private List<SpotifyImage> images;
	private String id;
	private String tracksUrl;
	
	@JsonProperty("tracks")
	private void unpackNested(Map<String, Object> tracks) {
		this.tracksUrl = (String) tracks.get("href");
	}

}
