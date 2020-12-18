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
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyUser;
import com.psb.service.SpotifyService;
import com.psb.util.PlaylistFileWriter;

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
		SpotifyPlaylists playlists = spotifyService.getPlaylists(oauthToken);
		PlaylistsResponse resp = new PlaylistsResponse();
		List<PlaylistResponse> spotifyPlaylists = new ArrayList<>();
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			PlaylistResponse playlistResponse = new PlaylistResponse();
			playlistResponse.setPlaylistName(playlist.getName());
			playlistResponse.setTracks(spotifyService.getPlaylistTracks(oauthToken, playlist));
			spotifyPlaylists.add(playlistResponse);
		}
		PlaylistFileWriter.writePlaylistsToFile(spotifyPlaylists);
		resp.setPlaylists(spotifyPlaylists);
		return resp;
	}
	
}
