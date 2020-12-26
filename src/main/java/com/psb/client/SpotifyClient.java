package com.psb.client;

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
public class SpotifyClient {
	
    WebClient client;
	
	@Autowired
	public SpotifyClient(WebClient webClient) {
		this.client = webClient;
	}
	
	private static final String GET_PLAYLISTS_URL = "/me/playlists";
	
	public SpotifyPlaylists getPlaylists(String oauthToken){
		return getPlaylistsWithPagination(oauthToken);
	}
	
	private SpotifyPlaylists getPlaylistsWithPagination(String oauthToken) {
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		String playlistsUrl = GET_PLAYLISTS_URL;
		List<SpotifyPlaylist> playlistsList = new ArrayList<>();
		while(playlistsUrl != null) {
			SpotifyPlaylists playlists = client.get().uri(playlistsUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve().bodyToMono(SpotifyPlaylists.class).block();
			playlistsList.addAll(playlists.getPlaylists());
			playlistsUrl = playlists.getNext();
		}
		spotifyPlaylists.setPlaylists(playlistsList);
		return spotifyPlaylists;
	}
	
	public SpotifyTracks getPlaylistTracks(String oauthToken, SpotifyPlaylist playlist) {
		return getPlaylistTracksWithPagination(oauthToken, playlist);
	}
	
	private SpotifyTracks getPlaylistTracksWithPagination(String oauthToken, SpotifyPlaylist playlist) {
		System.out.println("***********************************************");
		System.out.println("Getting " + playlist.getName() + " tracks");
		System.out.println("***********************************************");
		String tracksUrl = playlist.getTracksUrl();
		SpotifyTracks tracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		System.out.println(tracksUrl);
		while (tracksUrl != null) {
			System.out.println(tracksUrl);
			SpotifyTracks tempTracks = client.get().uri(tracksUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve().bodyToMono(SpotifyTracks.class).block();
			tracksList.addAll(tempTracks.getTracks());
			tracksUrl = tempTracks.getNext();
		}
		tracks.setTracks(tracksList);
		for (SpotifyTrack spotifyTrack : tracks.getTracks()) {
			System.out.println(spotifyTrack.getName());
		}
		return tracks;
	}
	

}
