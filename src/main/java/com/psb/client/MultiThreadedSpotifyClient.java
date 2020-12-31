package com.psb.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

public class MultiThreadedSpotifyClient implements Runnable {
	
	private WebClient client;
	private String oauthToken;
	private SpotifyPlaylist playlist;
	private SpotifyTracks tracks;
	
	public MultiThreadedSpotifyClient(WebClient client, String oauthToken, SpotifyPlaylist playlist){
		this.client = client;
		this.oauthToken = oauthToken;
		this.playlist = playlist;
	}
	
	public void run() 
    { 
        try
        { 
            // Displaying the thread that is running 
            System.out.println ("Thread " + 
                  Thread.currentThread().getId() + 
                  " is running");
            this.tracks = getPlaylistTracksWithPagination();
  
        } 
        catch (Exception e) 
        { 
            // Throwing an exception 
            System.out.println ("Exception is caught"); 
        } 

    } 
	
	private SpotifyTracks getPlaylistTracksWithPagination() {
		String tracksUrl = playlist.getTracksUrl();
		System.out.println("***********************************************");
		System.out.println("Getting " + playlist.getName() + " tracks");
		System.out.println("***********************************************");
		SpotifyTracks tracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		while (tracksUrl != null) {
			System.out.println("Calling endpoint " + tracksUrl);
			SpotifyTracks tempTracks = client.get().uri(tracksUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve().bodyToMono(SpotifyTracks.class).block();
			tracksList.addAll(tempTracks.getTracks());
			tracksUrl = tempTracks.getNext();
		}
		tracks.setTracks(tracksList);
		return tracks;
	}
	
	public SpotifyTracks getTracks() {
		return this.tracks;
	}

}
