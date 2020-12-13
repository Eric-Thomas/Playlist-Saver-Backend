package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.response.PlaylistResponse;
import com.psb.model.response.PlaylistsResponse;
import com.psb.model.spotify.Playlist;
import com.psb.model.spotify.Playlists;
import com.psb.model.spotify.SpotifyUser;
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
	public PlaylistsResponse savePlaylist(@RequestBody SpotifyUser spotifyUser) {
		String oauthToken = spotifyUser.getOauthToken();
		Playlists playlists = spotifyService.getPlaylists(oauthToken);
		PlaylistsResponse resp = new PlaylistsResponse();
		List<PlaylistResponse> spotifyPlaylists = new ArrayList<>();
		for (Playlist playlist : playlists.getPlaylists()) {
			PlaylistResponse playlistResponse = new PlaylistResponse();
			playlistResponse.setPlaylistName(playlist.getName());
			playlistResponse.setTracks(spotifyService.getPlaylistTracks(oauthToken, playlist));
			spotifyPlaylists.add(playlistResponse);
		}
		resp.setPlaylists(spotifyPlaylists);
		return resp;
	}
	
}
