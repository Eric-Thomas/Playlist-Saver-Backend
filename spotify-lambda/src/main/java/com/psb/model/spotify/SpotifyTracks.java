package com.psb.model.spotify;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyTracks implements Serializable {

	private static final long serialVersionUID = 2970232868076965341L;
	
	@JsonProperty("items")
	@JsonInclude(Include.NON_NULL)
	private List<SpotifyTrack> tracks;
	private String next;

}
