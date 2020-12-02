package com.psb.model;

import java.util.List;

import lombok.Data;

@Data
public class Track {
	
	private String name;
	private String uri;
	private Album album;
	private List<Artist> artists;

}
