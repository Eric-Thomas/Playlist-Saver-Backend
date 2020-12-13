package com.psb.model.spotify;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Playlist {
	
	private String name;
	private String tracksUrl;
	@JsonProperty("tracks")
	private void unpackNested(Map<String, Object> tracks) {
		this.tracksUrl = (String)tracks.get("href");
	}
	
}
