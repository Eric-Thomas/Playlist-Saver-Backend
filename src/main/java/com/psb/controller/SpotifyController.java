package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.repository.PlaylistInfo;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.thread.GetPlaylistTracksAndSaveToS3Thread;

@RestController
@RequestMapping("/spotify")
@CrossOrigin
public class SpotifyController {

	private SpotifyClient spotifyClient;
	private AWSS3Client s3Client;

	@Autowired
	public SpotifyController(SpotifyClient spotifyClient, AWSS3Client s3Client) {
		this.spotifyClient = spotifyClient;
		this.s3Client = s3Client;
	}

	@GetMapping(path = "/playlists/info")
	public List<PlaylistInfo> getPlaylistsInfo(@RequestHeader String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException {
		SpotifyPlaylists playlists = spotifyClient.getPlaylists(oauthToken);
		List<PlaylistInfo> response = new ArrayList<>();
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			PlaylistInfo playlistInfo = new PlaylistInfo();
			playlistInfo.setName(playlist.getName());
			if (!playlist.getImages().isEmpty()) {
				playlistInfo.setImageUri(playlist.getImages().get(0).getUrl());
			}
			playlistInfo.setId(playlist.getId());
			response.add(playlistInfo);
		}
		getPlaylistsTracksAndSaveToS3(oauthToken, playlists);
		return response;
	}

	private void getPlaylistsTracksAndSaveToS3(String oauthToken, SpotifyPlaylists playlists) {
		Thread t = new Thread(new GetPlaylistTracksAndSaveToS3Thread(oauthToken, playlists, spotifyClient, s3Client));
		t.start();
	}
}
