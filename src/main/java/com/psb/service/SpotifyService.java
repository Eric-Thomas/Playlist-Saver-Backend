package com.psb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Tracks;

@Service
public class SpotifyService {
	
    WebClient client;
	
	@Autowired
	public SpotifyService(WebClient webClient) {
		this.client = webClient;
	}
	
	private static final String GET_PLAYLISTS_URL = "/me/playlists";
	
	public Playlists getPlaylists(String oauthToken){
		Playlists playlists = client.get().uri(GET_PLAYLISTS_URL)
				.headers(httpHeaders -> {
					httpHeaders.setBearerAuth(oauthToken);
				}).retrieve().bodyToMono(Playlists.class).block();
		
		return playlists;
	}
	
	public Tracks getPlaylistTracks(String oauthToken, Playlist playlist) {
		String tracksUrl = playlist.getTracksUrl();
		Tracks tracks = client.get().uri(tracksUrl)
				.headers(httpHeaders -> {
					httpHeaders.setBearerAuth(oauthToken);
				}).retrieve().bodyToMono(Tracks.class).block();
		
		
		return tracks;
	}
	

}
