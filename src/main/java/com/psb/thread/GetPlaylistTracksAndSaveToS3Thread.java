package com.psb.thread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.SerializationUtils;

import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.repository.Playlist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyPlaylists;
import com.psb.model.spotify.SpotifyTracks;
import com.psb.util.Compresser;
import com.psb.util.SpotifyResponseConverter;

public class GetPlaylistTracksAndSaveToS3Thread implements Runnable {

	private String oauthToken;
	private SpotifyPlaylists playlists;
	private SpotifyClient spotifyClient;
	AWSS3Client s3Client;
	private SpotifyResponseConverter spotifyResponseConverter;
	@Autowired
	public GetPlaylistTracksAndSaveToS3Thread(String oauthToken, SpotifyPlaylists playlists,
			SpotifyClient spotifyClient, AWSS3Client s3Client) {
		this.oauthToken = oauthToken;
		this.spotifyResponseConverter = new SpotifyResponseConverter();
		this.playlists = playlists;
		this.spotifyClient = spotifyClient;
		this.s3Client = s3Client;
	}

	@Override
	public void run() {
		try {
		String folderPath = null;
		for (SpotifyPlaylist playlist : playlists.getPlaylists()) {
			Playlist repositoryPlaylist = getRepositoryPlaylist(playlist);
			if (folderPath == null) {
				folderPath = spotifyClient.getUserName(oauthToken);
			}
			// Spotify usernames are unique, so we'll use those to identify bucket objects
			String objectKey = folderPath + "/" + playlist.getName() + "/" + playlist.getId();
			saveToS3(objectKey, repositoryPlaylist);
		}
		} catch (SpotifyClientException | SpotifyClientUnauthorizedException e) {
			e.printStackTrace();
		} catch (AWSS3ClientException e ) {
			e.printStackTrace();
		}

	}
	
	private Playlist getRepositoryPlaylist(SpotifyPlaylist playlist) throws SpotifyClientException, SpotifyClientUnauthorizedException {
		SpotifyTracks tracks = spotifyClient.getPlaylistTracks(oauthToken, playlist);
		return spotifyResponseConverter.convertPlaylist(playlist, tracks);
	}
	
	private void saveToS3(String objectKey, Playlist playlist) throws AWSS3ClientException {
		byte[] data = Compresser.compress(SerializationUtils.serialize(playlist));
		s3Client.saveData(data, objectKey);
	}

}
