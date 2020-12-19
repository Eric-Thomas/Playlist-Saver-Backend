package com.psb.model.repository;

import java.util.List;

import lombok.Data;

@Data
public class Playlist {
	
	String playlistName;
	List<Track> tracks;

}
