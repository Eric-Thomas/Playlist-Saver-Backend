package com.psb.model.repository;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class Playlist implements Serializable {

	/**
	 * Generated serial version ID
	 */
	private static final long serialVersionUID = 1680150966024931641L;
	private String playlistName;
	private List<Track> tracks;

}
