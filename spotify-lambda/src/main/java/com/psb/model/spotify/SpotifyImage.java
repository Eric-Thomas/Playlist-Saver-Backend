package com.psb.model.spotify;

import java.io.Serializable;

import lombok.Data;

@Data
public class SpotifyImage implements Serializable{

	private static final long serialVersionUID = 757556048604592319L;
	
	private String height;
	private String width;
	private String url;

}
