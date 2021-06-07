package com.psb.model.spotify;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyPlaylist {

	private String name;
	private String tracksUrl;
	private List<SpotifyImage> images;
	private String id;

	@JsonProperty("tracks")
	private void unpackNested(Map<String, Object> tracks) {
		this.tracksUrl = (String) tracks.get("href");
	}

}
