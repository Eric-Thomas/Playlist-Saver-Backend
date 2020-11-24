package com.psb.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.SpotifyUser;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {
	
	@PostMapping(path = "/playlists", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public String savePlaylist(@RequestBody SpotifyUser spotifyUser) {
		return spotifyUser.toString();
	}
	
}
