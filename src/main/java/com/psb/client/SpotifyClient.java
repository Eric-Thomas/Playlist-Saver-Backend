package com.psb.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;

import reactor.core.publisher.Mono;

@Component
public class SpotifyClient {
	
    private WebClient client;
    
    @Value("${spotify.client.id}")
    private String clientId;
    @Value("${spotify.redirect.uri}")
    private String redirectUri;
    @Value("${spotify.scope}")
    private String scope;
    
    private Logger logger = Logger.getLogger(SpotifyClient.class.getName());
    
	private static final String GET_PLAYLISTS_PATH = "/me/playlists?limit=50";
	private static final String UNAUTHORIZED_ERROR_MESSAGE = "Invalid spotify oauth token.";
	
	@Autowired
	public SpotifyClient(WebClient webClient) {
		this.client = webClient;
	}
	
	public String login() {

        return client.get()
		.uri("https://accounts.spotify.com/authorize")
		.attribute("response_type", "code")
		.attribute("client_id", clientId)
		.attribute("scope", scope)
		.attribute("redirect_uri", redirectUri)
		.retrieve().bodyToMono(String.class).block();
	}
	
	public SpotifyPlaylists getPlaylists(String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException{
		return getPlaylistsWithPagination(oauthToken);
	}
	
	private SpotifyPlaylists getPlaylistsWithPagination(String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException{
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		String playlistsUrl = GET_PLAYLISTS_PATH;
		List<SpotifyPlaylist> playlistsList = new ArrayList<>();
		while(playlistsUrl != null) {
			logger.info("Getting playlists at " + playlistsUrl);
			SpotifyPlaylists playlists = client.get().uri(playlistsUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve()
		            .onStatus(HttpStatus::isError, response -> {
		            	if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
		            		return Mono.error(new SpotifyClientUnauthorizedException(
		            				UNAUTHORIZED_ERROR_MESSAGE));
		            	} else {
			            	return Mono.error(new SpotifyClientException(response.statusCode().toString()));
		            	}
		            })
		            .bodyToMono(SpotifyPlaylists.class).block();
			playlistsList.addAll(playlists.getPlaylists());
			playlistsUrl = playlists.getNext();
		}
		spotifyPlaylists.setPlaylists(playlistsList);
		return spotifyPlaylists;
	}
	
	public SpotifyTracks getPlaylistTracks(String oauthToken, SpotifyPlaylist playlist) throws SpotifyClientException{
		MultiThreadedSpotifyClient threadedClient = new MultiThreadedSpotifyClient(client, oauthToken, playlist);
		Thread thread = new Thread(threadedClient);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		}
		if (threadedClient.getErrorMessage() != null) {
			throw new SpotifyClientException(threadedClient.getErrorMessage());
		}
		return threadedClient.getTracks();
	}

}
