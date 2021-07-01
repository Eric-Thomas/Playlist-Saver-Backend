package com.psb.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.s3.S3Playlist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.Compresser;

public class GetPlaylistTracksAndSaveToS3Thread implements Runnable {

	private String oauthToken;
	private SpotifyPlaylists playlists;
	private SpotifyClient spotifyClient;
	AWSS3Client s3Client;

	@Autowired
	public GetPlaylistTracksAndSaveToS3Thread(String oauthToken, SpotifyPlaylists playlists,
			SpotifyClient spotifyClient, AWSS3Client s3Client) {
		this.oauthToken = oauthToken;
		this.playlists = playlists;
		this.spotifyClient = spotifyClient;
		this.s3Client = s3Client;
	}

	@Override
	public void run() {
		try {
		String folderPath = null;
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			if (folderPath == null) {
				folderPath = spotifyClient.getUserID(oauthToken);
			}
			// Spotify userIDs are unique, so we'll use those to identify bucket objects
			String objectKey = folderPath + "/" + playlist.getId();
			SpotifyTracks tracks = spotifyClient.getPlaylistTracks(oauthToken, playlist);
			S3Playlist s3Playlist = new S3Playlist(playlist, tracks);
			saveToS3(objectKey, s3Playlist);
		}
		} catch (SpotifyClientException | SpotifyClientUnauthorizedException e) {
			e.printStackTrace();
		} catch (AWSS3ClientException e ) {
			e.printStackTrace();
		}

	}

	
	private void saveToS3(String objectKey, S3Playlist playlist) throws AWSS3ClientException {
		byte[] data = Compresser.compress(SerializationUtils.serialize(playlist));
		s3Client.saveData(data, objectKey);
	}

}
