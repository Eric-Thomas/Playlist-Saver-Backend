package com.psb.model.spotify;

import java.io.Serializable;

import lombok.Data;

@Data
public class SpotifyArtist implements Serializable {

	private static final long serialVersionUID = -8471680861739472790L;
	
	private String name;

}
