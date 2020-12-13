package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.SpotifyUser;
import com.psb.model.Tracks;
import com.psb.service.SpotifyService;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {
	
	private SpotifyService spotifyService;
	
	@Autowired
	public SpotifyController(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}
	
	@PostMapping(path = "/playlists", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public List<Tracks> savePlaylist(@RequestBody SpotifyUser spotifyUser) {
		String oauthToken = spotifyUser.getOauthToken();
		Playlists playlists = spotifyService.getPlaylists(oauthToken);
		List<Tracks> tracks = new ArrayList<>();
		for (Playlist playlist : playlists.getPlaylists()) {
			tracks.add(spotifyService.getPlaylistTracks(oauthToken, playlist));
		}
		return tracks;
	}
	
}
