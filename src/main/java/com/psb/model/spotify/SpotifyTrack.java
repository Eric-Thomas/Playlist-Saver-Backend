package com.psb.model.spotify;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class SpotifyTrack {
	
	private String name;
	private String uri;
	private SpotifyAlbum album;
	private List<SpotifyArtist> artists;
    @JsonProperty("track")
    private void unpackNested(Map<String,Object> track) {
    	ObjectMapper mapper = new ObjectMapper()
    			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.name = (String) track.get("name");
        this.uri = (String) track.get("uri");
        try {
			String albumJson = mapper.writeValueAsString(track.get("album"));
			SpotifyAlbum album = mapper.readValue(albumJson, SpotifyAlbum.class);
			this.album = album;
			String artistsJson = mapper.writeValueAsString(track.get("artists"));
			List<SpotifyArtist> artists = mapper.readValue(
				      artistsJson, new TypeReference<List<SpotifyArtist>>() { });
			this.artists = artists;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }

}
