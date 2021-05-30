package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.Compresser;
import com.psb.util.SpotifyResponseConverter;

@RestController
@RequestMapping("/spotify")
@SessionAttributes("oauth")
public class SpotifyController {

	private SpotifyClient spotifyClient;
	private SpotifyResponseConverter spotifyResponseConverter;
	private AWSS3Client s3Client;

	@Autowired
	public SpotifyController(SpotifyClient spotifyClient, SpotifyResponseConverter spotifyResponseConverter, AWSS3Client s3Client) {
		this.spotifyClient = spotifyClient;
		this.spotifyResponseConverter = spotifyResponseConverter;
		this.s3Client = s3Client;
	}

	@GetMapping(path = "/playlists")
	public Playlists savePlaylist(@RequestHeader String oauthToken)
			throws SpotifyClientException, SpotifyClientUnauthorizedException, AWSS3ClientException {
		SpotifyPlaylists playlists = spotifyClient.getPlaylists(oauthToken);
		Playlists resp = new Playlists();
		List<Playlist> spotifyPlaylists = new ArrayList<>();
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			SpotifyTracks tracks = spotifyClient.getPlaylistTracks(oauthToken, playlist);
			Playlist repositoryPlaylist = spotifyResponseConverter.convertPlaylist(playlist, tracks);
			spotifyPlaylists.add(repositoryPlaylist);
		}
		resp.setPlaylists(spotifyPlaylists);
		// Spotify usernames are unique, so we'll use those to identify bucket objects
		String objectKey = spotifyClient.getUserName(oauthToken);
		byte[] data = Compresser.compress(SerializationUtils.serialize(spotifyPlaylists));
		s3Client.saveData(data, objectKey);
		return resp;
	}
}
