package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.psb.client.SpotifyClient;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.SpotifyResponseConverter;

@RestController
@RequestMapping("/spotify")
@SessionAttributes("oauth")
public class SpotifyController {
	
	private SpotifyClient spotifyClient;
	private SpotifyResponseConverter spotifyResponseConverter;
	
	@Autowired
	public SpotifyController(SpotifyClient spotifyClient,
			SpotifyResponseConverter spotifyResponseConverter) {
		this.spotifyClient = spotifyClient;
		this.spotifyResponseConverter = spotifyResponseConverter;
	}
	
	@GetMapping(path = "/playlists")
	public Playlists savePlaylist(@RequestHeader String oauthToken) {
		SpotifyPlaylists playlists = spotifyClient.getPlaylists(oauthToken);
		Playlists resp = new Playlists();
		List<Playlist> spotifyPlaylists = new ArrayList<>();
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			SpotifyTracks tracks = spotifyClient.getPlaylistTracks(
					oauthToken, playlist);
			Playlist repositoryPlaylist = 
					spotifyResponseConverter.convertPlaylist(playlist, tracks);
			spotifyPlaylists.add(repositoryPlaylist);
		}
		resp.setPlaylists(spotifyPlaylists);
		return resp;
	}
	
	@GetMapping(path = "/login")
	public String login() {
		return spotifyClient.login();
	}
}
