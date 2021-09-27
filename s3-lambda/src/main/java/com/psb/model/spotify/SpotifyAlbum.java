package com.psb.model.spotify;

import java.io.Serializable;

import lombok.Data;

@Data
public class SpotifyAlbum implements Serializable{

	private static final long serialVersionUID = 4760421128053773421L;
	
	private String name;

}
