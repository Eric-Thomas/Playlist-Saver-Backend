package com.psb.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;

@Component
public class SpotifyClient {
	
    WebClient client;
    
    @Value("${spotify.client.id}")
    private String clientId;
    @Value("${spotify.redirect.uri}")
    private String redirectUri;
    @Value("${spotify.scope}")
    private String scope;
    
    private Logger logger = Logger.getLogger(SpotifyClient.class.getName());
	
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
			logger.info("Getting playlists at " + playlistsUrl);
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
		MultiThreadedSpotifyClient threadedClient = new MultiThreadedSpotifyClient(client, oauthToken, playlist);
		Thread thread = new Thread(threadedClient);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return threadedClient.getTracks();
	}
	

}
