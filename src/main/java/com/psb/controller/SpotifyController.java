package com.psb.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.model.repository.Playlist;
import com.psb.model.repository.Playlists;
import com.psb.model.repository.S3Response;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.model.spotify.SpotifyUser;
import com.psb.service.SpotifyService;
import com.psb.util.FileReader;
import com.psb.util.PlaylistFileWriter;
import com.psb.util.SpotifyResponseConverter;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@RestController
@RequestMapping("/spotify")
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
	
	@PostMapping(path = "/save-count", consumes = {MediaType.APPLICATION_JSON_VALUE})
	public S3Response saveCount(@RequestBody SpotifyUser spotifyUser) {
		String oauthToken = spotifyUser.getOauthToken();
		SpotifyPlaylists playlists = spotifyService.getPlaylists(oauthToken);
		S3Response response = new S3Response();
		response.setCount(playlists.getPlaylists().size());
		
	    String objectKey = "helloworld.txt"; 
	    String objectPath = "./src/test/resources/com/psb/helloworld.txt";
	    String result = "";
        try {
            PutObjectResponse s3Response = s3.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(objectKey)
                            .build(),
                            software.amazon.awssdk.core.sync.RequestBody.fromBytes(FileReader.getObjectFile(objectPath)));
            result = s3Response.eTag();
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
	
}
