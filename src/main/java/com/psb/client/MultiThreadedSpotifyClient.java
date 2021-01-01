package com.psb.client;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

import reactor.core.publisher.Mono;

public class MultiThreadedSpotifyClient implements Runnable {
	
	private WebClient client;
	private String oauthToken;
	private SpotifyPlaylist playlist;
	private SpotifyTracks tracks;
	private String errorMessage;
	private Logger logger = Logger.getLogger(MultiThreadedSpotifyClient.class.getName());
	
	private static final String UNAUTHORIZED_ERROR_MESSAGE = "Invalid spotify oauth token.";
	
	public MultiThreadedSpotifyClient(WebClient client, String oauthToken, SpotifyPlaylist playlist){
		this.client = client;
		this.oauthToken = oauthToken;
		this.playlist = playlist;
	}
	
	public void run(){ 
        logger.info ("Thread " + 
              Thread.currentThread().getId() + 
              " is running");
        try {
			this.tracks = getPlaylistTracksWithPagination();
		} catch (Exception e) {
			this.errorMessage = e.getMessage();
		}
    } 
	
	private SpotifyTracks getPlaylistTracksWithPagination() throws SpotifyClientException{
		String tracksUrl = playlist.getTracksUrl();
		logger.info("***********************************************");
		logger.info("Getting " + playlist.getName() + " tracks");
		logger.info("***********************************************");
		SpotifyTracks tracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		while (tracksUrl != null) {
			logger.info("Calling endpoint " + tracksUrl);
			SpotifyTracks tempTracks = client.get().uri(tracksUrl)
					.headers(httpHeaders -> {
						httpHeaders.setBearerAuth(oauthToken);
					}).retrieve()
		            .onStatus(HttpStatus::isError, response ->{ 
		            	if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
		            		return Mono.error(new SpotifyClientUnauthorizedException(
		            				UNAUTHORIZED_ERROR_MESSAGE));
		            	} else {
			            	return Mono.error(new SpotifyClientException(response.statusCode().toString()));
		            	}
		            })
		            .bodyToMono(SpotifyTracks.class).block();
			tracksList.addAll(tempTracks.getTracks());
			tracksUrl = tempTracks.getNext();
		}
		tracks.setTracks(tracksList);
		return tracks;
	}
	
	public SpotifyTracks getTracks() {
		return this.tracks;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}

}
