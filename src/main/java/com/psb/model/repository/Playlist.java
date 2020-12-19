package com.psb.model.repository;

import java.util.List;

import lombok.Data;

@Data
public class Playlist {
	
	private String playlistName;
	private List<Track> tracks;

}
