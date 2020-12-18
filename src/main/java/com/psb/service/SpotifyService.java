package com.psb.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

@Service
public class SpotifyService {
	
    WebClient client;
	
	@Autowired
	public SpotifyService(WebClient webClient) {
		this.client = webClient;
	}
	
	private static final String GET_PLAYLISTS_URL = "/me/playlists";
	
	public SpotifyPlaylists getPlaylists(String oauthToken){
		SpotifyPlaylists playlists = client.get().uri(GET_PLAYLISTS_URL)
				.headers(httpHeaders -> {
					httpHeaders.setBearerAuth(oauthToken);
				}).retrieve().bodyToMono(SpotifyPlaylists.class).block();
		
		return playlists;
	}
	
	public SpotifyTracks getPlaylistTracks(String oauthToken, SpotifyPlaylist playlist) {
		return getPlaylistTracksWithPagination(oauthToken, playlist);
	}
	
	private SpotifyTracks getPlaylistTracksWithPagination(String oauthToken, SpotifyPlaylist playlist) {
		String tracksUrl = playlist.getTracksUrl();
		SpotifyTracks tracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		while (tracksUrl != null) {
			System.out.println("tracksURL: " + tracksUrl);
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
	

}
