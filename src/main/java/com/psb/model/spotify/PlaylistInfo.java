package com.psb.model.spotify;

import java.io.Serializable;

import lombok.Data;

@Data
public class PlaylistInfo implements Serializable {
	
	private static final long serialVersionUID = 8201458729923746288L;
	
	private String name;
	private String imageUri;
	private String id;

}
