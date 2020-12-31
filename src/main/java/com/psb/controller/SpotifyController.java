package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.repository.S3Response;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.util.Compresser;
import com.psb.util.SpotifyResponseConverter;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/spotify")
@SessionAttributes("oauth")
public class SpotifyController {
	
	private SpotifyClient spotifyClient;
	private SpotifyResponseConverter spotifyResponseConverter;
	private AWSS3Client s3Client;
	
	@Autowired
	public SpotifyController(SpotifyClient spotifyClient,
			SpotifyResponseConverter spotifyResponseConverter,
			AWSS3Client s3Client) {
		this.spotifyClient = spotifyClient;
		this.spotifyResponseConverter = spotifyResponseConverter;
		this.s3Client = s3Client;
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
	
	@PutMapping(path = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public S3Response save(@RequestBody SpotifyUser spotifyUser) {
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
	    byte[] data = Compresser.compress(SerializationUtils.serialize(spotifyUser.getPlaylists()));
	    S3Response response = s3Client.saveData(data, objectKey);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(path = "/load", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists load(@RequestBody SpotifyUser spotifyUser) {
		Playlists playlists = new Playlists();
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getData(objectKey);
        Object object = SerializationUtils.deserialize(Compresser.decompress(objectBytes.asByteArray()));
        playlists.setPlaylists((List<Playlist>) object);
		return playlists;
	}
	
	@GetMapping(path = "/login")
	public String login() {
		return spotifyClient.login();
	}
}
