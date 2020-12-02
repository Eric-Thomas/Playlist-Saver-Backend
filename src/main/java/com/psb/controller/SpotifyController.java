package com.psb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.SpotifyUser;
import com.psb.service.SpotifyService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {
	
	private SpotifyService spotifyService;
	
	@Autowired
	public SpotifyController(SpotifyService spotifyService) {
		this.spotifyService = spotifyService;
	}
	
	@PostMapping(path = "/playlists", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists savePlaylist(@RequestBody SpotifyUser spotifyUser) {
		return spotifyService.getPlaylists(spotifyUser.getOauthToken());
	}
	
}
