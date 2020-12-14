package com.psb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.Playlist;
import com.psb.model.spotify.Playlists;
import com.psb.model.spotify.Track;
import com.psb.model.spotify.Tracks;

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
		return getPlaylistTracksWithPagination(oauthToken, playlist);
	}
	
	private Tracks getPlaylistTracksWithPagination(String oauthToken, Playlist playlist) {
		String tracksUrl = playlist.getTracksUrl();
		Tracks tracks = new Tracks();
		List<Track> tracksList = new ArrayList<>();
		while (tracksUrl != null) {
			System.out.println("tracksURL: " + tracksUrl);
			Tracks tempTracks = client.get().uri(tracksUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve().bodyToMono(Tracks.class).block();
			tracksList.addAll(tempTracks.getTracks());
			tracksUrl = tempTracks.getNext();
		}
		tracks.setTracks(tracksList);
		return tracks;
	}
	

}
