package com.psb.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Tracks {
	
	private String href;
	@JsonProperty("items")
	List<TrackItem> trackItems;
	
	public List<Track> fetchAllTracks() {
		List<Track> tracks = new LinkedList<>();
		for (TrackItem trackItem : trackItems) {
			tracks.add(trackItem.getTrack());
		}
		
		return tracks;
	}

}
