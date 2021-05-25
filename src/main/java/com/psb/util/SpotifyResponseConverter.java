package com.psb.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.psb.model.repository.Album;
import com.psb.model.repository.Artist;
import com.psb.model.repository.Playlist;
import com.psb.model.repository.Track;
import com.psb.model.spotify.SpotifyAlbum;
import com.psb.model.spotify.SpotifyArtist;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.model.spotify.SpotifyTrack;
import com.psb.model.spotify.SpotifyTracks;

@Component
public class SpotifyResponseConverter {

	public SpotifyResponseConverter() {

	}

	public Playlist convertPlaylist(SpotifyPlaylist spotifyPlaylist, SpotifyTracks spotifyTracks) {
		Playlist repositoryPlaylist = new Playlist();
		repositoryPlaylist.setPlaylistName(spotifyPlaylist.getName());
		repositoryPlaylist.setTracks(convertTracks(spotifyTracks));
		return repositoryPlaylist;
	}

	private List<Track> convertTracks(SpotifyTracks spotifyTracks) {
		List<Track> repositoryTracks = new ArrayList<>();
		if (spotifyTracks != null) {
			for (SpotifyTrack spotifyTrack : spotifyTracks.getTracks()) {
				Track track = new Track();
				track.setName(spotifyTrack.getName());
				track.setUri(spotifyTrack.getUri());
				track.setAlbum(convertAlbum(spotifyTrack.getAlbum()));
				track.setArtists(convertArtists(spotifyTrack.getArtists()));
				repositoryTracks.add(track);
			}
		}
		return repositoryTracks;

	}

	private List<Artist> convertArtists(List<SpotifyArtist> artists) {
		List<Artist> repositoryArtists = new ArrayList<>();
		if (artists != null) {
			for (SpotifyArtist spotifyArtist : artists) {
				Artist artist = new Artist();
				artist.setName(spotifyArtist.getName());
				repositoryArtists.add(artist);
			}
		}
		return repositoryArtists;
	}

	private Album convertAlbum(SpotifyAlbum album) {
		Album repositoryAlbum = new Album();
		if (album != null) {
			repositoryAlbum.setName(album.getName());
		}
		return repositoryAlbum;
	}

}
