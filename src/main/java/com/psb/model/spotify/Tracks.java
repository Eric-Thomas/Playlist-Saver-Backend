package com.psb.model.spotify;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tracks {
	
	@JsonProperty("items")
	List<Track> tracks;
	String next;

}
