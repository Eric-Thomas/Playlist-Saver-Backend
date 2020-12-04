package com.psb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Playlists {

	private String href;
	@JsonProperty("items")
	List<Playlist> playlists;
	
}
