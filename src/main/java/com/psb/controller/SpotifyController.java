package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.service.SpotifyService;
import com.psb.util.PlaylistFileWriter;
import com.psb.util.SpotifyResponseConverter;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {
	
	private SpotifyService spotifyService;
	private SpotifyResponseConverter spotifyResponseConverter;
	
	@Autowired
	public SpotifyController(SpotifyService spotifyService,
			SpotifyResponseConverter spotifyResponseConverter) {
		this.spotifyService = spotifyService;
		this.spotifyResponseConverter = spotifyResponseConverter;
	}
	
	@PostMapping(path = "/playlists", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists savePlaylist(@RequestBody SpotifyUser spotifyUser) {
		String oauthToken = spotifyUser.getOauthToken();
		SpotifyPlaylists playlists = spotifyService.getPlaylists(oauthToken);
		Playlists resp = new Playlists();
		List<Playlist> spotifyPlaylists = new ArrayList<>();
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			SpotifyTracks tracks = spotifyService.getPlaylistTracks(
					oauthToken, playlist);
			Playlist repositoryPlaylist = 
					spotifyResponseConverter.convertPlaylist(playlist, tracks);
			spotifyPlaylists.add(repositoryPlaylist);
		}
		PlaylistFileWriter.writePlaylistsToFile(spotifyPlaylists);
		resp.setPlaylists(spotifyPlaylists);
		return resp;
	}
	
}
