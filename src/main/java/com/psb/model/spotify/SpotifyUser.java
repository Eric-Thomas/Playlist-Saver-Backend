package com.psb.model.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyUser {

	@JsonProperty("display_name")
	private String displayName;
	private String id;

}
