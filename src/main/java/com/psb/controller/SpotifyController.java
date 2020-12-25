package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.SerializationUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.repository.S3Response;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.service.SpotifyService;
import com.psb.util.Compresser;
import com.psb.util.PlaylistFileWriter;
import com.psb.util.SpotifyResponseConverter;

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/spotify")
@SessionAttributes("oauth")
public class SpotifyController {
	
	private SpotifyService spotifyService;
	private SpotifyResponseConverter spotifyResponseConverter;
	
	@Value("${aws.bucket.name}")
	private String bucketName;
	private Region region = Region.US_EAST_2;
	private S3Client s3;
	
	@PostConstruct
	public void init() {
		 s3 = S3Client.builder().region(region).build();
		 System.out.println("S3 client built.");
	}
	
	@PreDestroy
	public void tini() {
		s3.close();
		System.out.println("S3 client closed.");
	}
	
	@Autowired
	public SpotifyController(SpotifyService spotifyService,
			SpotifyResponseConverter spotifyResponseConverter) {
		this.spotifyService = spotifyService;
		this.spotifyResponseConverter = spotifyResponseConverter;
	}
	
	@GetMapping(path = "/playlists", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists savePlaylist(@RequestBody SpotifyUser spotifyUser, @ModelAttribute String oauth) {
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
		resp.setPlaylists(spotifyPlaylists);
		return resp;
	}
	
	@PutMapping(path = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public S3Response save(@RequestBody SpotifyUser spotifyUser) {
		S3Response response = new S3Response();
		
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
	    byte[] data = Compresser.compress(SerializationUtils.serialize(spotifyUser.getPlaylists()));
		response.setKilobytes((int) data.length / 1024);
		System.out.println("Data size: " + response.getKilobytes() + "kB");
	    String result = "";
        try {
        	// LMAO java doesn't have import aliasing so one RequestBody must use the fully qualified name
            PutObjectResponse s3Response = s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                            software.amazon.awssdk.core.sync.RequestBody.fromBytes(data)); 
            result = s3Response.eTag(); // eTag is AWS's object hash, i.e. ideally unique ID
            response.setSuccess(true);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            result = e.getMessage();
            response.setSuccess(false);
        } 
        response.setResult(result);
	    System.out.println("Tag information: " + result);
	    
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GetMapping(path = "/load", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public Playlists load(@RequestBody SpotifyUser spotifyUser) {
		Playlists playlists = new Playlists();
		// Spotify usernames are unique, so we'll use those to identify bucket objects
	    String objectKey = spotifyUser.getUsername();
        try {
        	// LMAO java doesn't have import aliasing so one RequestBody must use the fully qualified name
            GetObjectRequest s3Request = GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(s3Request);
            Object object = SerializationUtils.deserialize(Compresser.decompress(objectBytes.asByteArray()));
            playlists.setPlaylists((List<Playlist>) object);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } 
		return playlists;
	}
}
