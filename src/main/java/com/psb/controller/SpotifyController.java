package com.psb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.*;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.s3.S3Playlist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.util.Compresser;

@RestController
@RequestMapping("/spotify")
@CrossOrigin
public class SpotifyController {

	private final String DELIMETER = "/";
	private SpotifyClient spotifyClient;
	private AWSS3Client s3Client;

	@Autowired
	public SpotifyController(SpotifyClient spotifyClient, AWSS3Client s3Client) {
		this.spotifyClient = spotifyClient;
		this.s3Client = s3Client;
	}

	@PostMapping(path = "/playlists/save")
	public void savePlaylists(@RequestHeader String oauthToken, @RequestBody  List<SpotifyPlaylist> playlists)
			throws SpotifyClientUnauthorizedException, SpotifyClientException, AWSS3ClientException {
		String folderPath = null;
		for (SpotifyPlaylist playlist : playlists) {
			if (folderPath == null) {
				SpotifyUser user = spotifyClient.getUser(oauthToken);
				folderPath = user.getId() + DELIMETER + user.getDisplayName();
			}
			// Spotify userIDs are unique, so we'll use those to identify bucket objects
			String objectKey = folderPath + DELIMETER + playlist.getId();
			SpotifyTracks tracks = spotifyClient.getPlaylistTracks(oauthToken, playlist);
			S3Playlist s3Playlist = new S3Playlist(playlist, tracks);
			saveToS3(objectKey, s3Playlist);

		}
	}

	private void saveToS3(String objectKey, S3Playlist playlist) throws AWSS3ClientException {
		byte[] data = Compresser.compress(SerializationUtils.serialize(playlist));
		s3Client.saveData(data, objectKey);
	}
}
