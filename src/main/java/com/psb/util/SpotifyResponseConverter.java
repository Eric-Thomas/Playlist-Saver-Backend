package com.psb.util;

import java.util.ArrayList;
import java.util.List;

import com.psb.model.repository.Album;
import com.psb.model.repository.Artist;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Track;
import com.psb.model.spotify.SpotifyAlbum;
import com.psb.model.spotify.SpotifyArtist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

public class SpotifyResponseConverter {
	
	public static Playlist convertPlaylist
	(SpotifyPlaylist spotifyPlaylist, SpotifyTracks spotifyTracks) {
		Playlist repositoryPlaylist = new Playlist();
		repositoryPlaylist.setPlaylistName(spotifyPlaylist.getName());
		repositoryPlaylist.setTracks(
				SpotifyResponseConverter.convertTracks(spotifyTracks));
		return repositoryPlaylist;
	}
	
	private static List<Track> convertTracks
	(SpotifyTracks spotifyTracks) {
		List<Track> repositoryTracks = new ArrayList<>();
		for (SpotifyTrack spotifyTrack : spotifyTracks.getTracks()) {
			Track track = new Track();
			track.setName(spotifyTrack.getName());
			track.setUri(spotifyTrack.getUri());
			track.setAlbum(
					SpotifyResponseConverter.convertAlbum(
							spotifyTrack.getAlbum()));
			track.setArtists(
					SpotifyResponseConverter.convertArtists(
							spotifyTrack.getArtists()));
			repositoryTracks.add(track);
		}
		return repositoryTracks;
				
	}

	private static List<Artist> convertArtists(List<SpotifyArtist> artists) {
		List<Artist> repositoryArtists = new ArrayList<>();
		for (SpotifyArtist spotifyArtist: artists) {
			Artist artist = new Artist();
			artist.setName(spotifyArtist.getName());
			repositoryArtists.add(artist);
		}
		return repositoryArtists;
	}

	private static Album convertAlbum(SpotifyAlbum album) {
		Album repositoryAlbum = new Album();
		repositoryAlbum.setName(album.getName());
		return repositoryAlbum;
	}


}
