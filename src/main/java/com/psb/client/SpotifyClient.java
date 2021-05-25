package com.psb.client;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

import reactor.core.publisher.Mono;

@Component
public class SpotifyClient {

	@Value("${spotify.playlists.uri}")
	private String playlistsUrl;

	private WebClient client;

	private Logger logger = LoggerFactory.getLogger(SpotifyClient.class);

	private static final String UNAUTHORIZED_ERROR_MESSAGE = "Invalid spotify oauth token.";

	@Autowired
	public SpotifyClient(WebClient webClient) {
		this.client = webClient;
	}

	public SpotifyPlaylists getPlaylists(String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException {
		return getPlaylistsWithPagination(oauthToken);
	}

	private SpotifyPlaylists getPlaylistsWithPagination(String oauthToken) {
		SpotifyPlaylists spotifyPlaylists = new SpotifyPlaylists();
		List<SpotifyPlaylist> playlistsList = new ArrayList<>();
		while (playlistsUrl != null) {
			logger.info("Getting playlists at {}", playlistsUrl);
			SpotifyPlaylists playlists = client.get().uri(playlistsUrl).headers(httpHeaders -> {
				httpHeaders.setBearerAuth(oauthToken);
			}).retrieve().onStatus(HttpStatus::isError, response -> {
				if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
					return Mono.error(new SpotifyClientUnauthorizedException(UNAUTHORIZED_ERROR_MESSAGE));
				} else {
					return Mono.error(new SpotifyClientException(response.statusCode().toString()));
				}
			}).bodyToMono(SpotifyPlaylists.class).block();
			playlistsList.addAll(playlists.getPlaylists());
			playlistsUrl = playlists.getNext();
		}
		spotifyPlaylists.setPlaylists(playlistsList);
		return spotifyPlaylists;
	}

	public SpotifyTracks getPlaylistTracks(String oauthToken, SpotifyPlaylist playlist) throws SpotifyClientException {
		return getPlaylistTracksWithPagination(oauthToken, playlist);
	}

	private SpotifyTracks getPlaylistTracksWithPagination(String oauthToken, SpotifyPlaylist playlist)
			throws SpotifyClientException {
		logger.info("***********************************************");
		logger.info("Getting {} tracks", playlist.getName());
		logger.info("***********************************************");
		String tracksUrl = playlist.getTracksUrl();
		SpotifyTracks tracks = new SpotifyTracks();
		List<SpotifyTrack> tracksList = new ArrayList<>();
		while (tracksUrl != null) {
			SpotifyTracks tempTracks = client.get().uri(tracksUrl).headers(httpHeaders -> {
				httpHeaders.setBearerAuth(oauthToken);
			}).retrieve().onStatus(HttpStatus::isError, response -> {
				if (response.statusCode() == HttpStatus.UNAUTHORIZED) {
					return Mono.error(new SpotifyClientUnauthorizedException(UNAUTHORIZED_ERROR_MESSAGE));
				} else {
					return Mono.error(new SpotifyClientException(response.statusCode().toString()));
				}
			}).bodyToMono(SpotifyTracks.class).block();
			tracksList.addAll(tempTracks.getTracks());
			tracksUrl = tempTracks.getNext();
		}
		tracks.setTracks(tracksList);
		for (SpotifyTrack spotifyTrack : tracks.getTracks()) {
			logger.info("Track: {}", spotifyTrack.getName());
		}
		return tracks;
	}

}
