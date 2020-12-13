package com.psb.model;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class Track {
	
	private String name;
	private String uri;
	private Album album;
	private List<Artist> artists;
    @JsonProperty("track")
    private void unpackNested(Map<String,Object> track) {
    	ObjectMapper mapper = new ObjectMapper()
    			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.name = (String) track.get("name");
        this.uri = (String) track.get("uri");
        try {
			String json = mapper.writeValueAsString(track.get("album"));
			Album album = mapper.readValue(json, Album.class);
			this.album = album;
			String json2 = mapper.writeValueAsString(track.get("artists"));
			List<Artist> artists = mapper.readValue(
				      json2, new TypeReference<List<Artist>>() { });
			this.artists = artists;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }

}
