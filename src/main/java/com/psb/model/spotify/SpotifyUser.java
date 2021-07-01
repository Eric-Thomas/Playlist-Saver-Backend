package com.psb.model.spotify;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SpotifyUser implements Serializable {

	private static final long serialVersionUID = 2368319712600970128L;
	
	@JsonProperty("display_name")
	private String displayName;
	private String id;

}
