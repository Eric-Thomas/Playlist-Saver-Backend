package com.psb.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.EncodingUtil;

import reactor.core.publisher.Mono;

@Component
public class SpotifyClient {
	
    WebClient client;
    
    @Value("${spotify.client.id}")
    private String clientId;
    @Value("${spotify.redirect.uri}")
    private String redirectUri;
    @Value("${spotify.scope}")
    private String scope;
	
	@Autowired
	public SpotifyClient(WebClient webClient) {
		this.client = webClient;
	}
	
	private static final String GET_PLAYLISTS_PATH = "/me/playlists?limit=50";
	
	public String login() {

        return client.get()
		.uri("https://accounts.spotify.com/authorize")
		.attribute("response_type", "code")
		.attribute("client_id", clientId)
		.attribute("scope", scope)
		.attribute("redirect_uri", redirectUri)
		.retrieve().bodyToMono(String.class).block();
	}
	
	public SpotifyPlaylists getPlaylists(String oauthToken){
		return getPlaylistsWithPagination(oauthToken);
	}
	
	private SpotifyPlaylists getPlaylistsWithPagination(String oauthToken) {
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		String playlistsUrl = GET_PLAYLISTS_PATH;
		List<SpotifyPlaylist> playlistsList = new ArrayList<>();
		while(playlistsUrl != null) {
			System.out.println("Getting playlists at " + playlistsUrl);
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
	

}
