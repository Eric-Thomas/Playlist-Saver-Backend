package com.psb.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.client.AWSS3Client;
import com.psb.exception.AWSS3ClientException;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.repository.S3Response;
import com.psb.model.spotify.SpotifyUser;
import com.psb.util.Compresser;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/s3")
public class S3Controller {
	
	private AWSS3Client s3Client;
	
	public S3Controller(AWSS3Client s3Client) {
		this.s3Client = s3Client;
	}
	
	@PutMapping(path = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public S3Response save(@RequestBody SpotifyUser spotifyUser) throws AWSS3ClientException {
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
	    byte[] data = Compresser.compress(SerializationUtils.serialize(spotifyUser.getPlaylists()));
	    return s3Client.saveData(data, objectKey);
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(path = "/load", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists load(@RequestBody SpotifyUser spotifyUser) throws AWSS3ClientException {
		Playlists playlists = new Playlists();
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getData(objectKey);
        Object object = SerializationUtils.deserialize(Compresser.decompress(objectBytes.asByteArray()));
        playlists.setPlaylists((List<Playlist>) object);
		return playlists;
	}

}
