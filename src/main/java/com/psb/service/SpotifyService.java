package com.psb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.psb.model.Artist;
import com.psb.model.Playlist;
import com.psb.model.Playlists;
import com.psb.model.Track;
import com.psb.model.TrackItem;
import com.psb.model.Tracks;

@Service
public class SpotifyService {
	
    WebClient client;
	
	@Autowired
	public SpotifyService(WebClient webClient) {
		this.client = webClient;
	}
	
	private static final String GET_PLAYLISTS_URL = "/me/playlists";
	
	public Playlists getPlaylists(String oauthToken){
		Playlists playlists = client.get().uri(GET_PLAYLISTS_URL)
				.headers(httpHeaders -> {
					httpHeaders.setBearerAuth(oauthToken);
				}).retrieve().bodyToMono(Playlists.class).block();
		
		for (Playlist playlist: playlists.getPlaylists()) {
			getPlaylistTracks(oauthToken, playlist.getTracks().getHref());
			break;
		}
		return playlists;
	}
	
	public void getPlaylistTracks(String oauthToken, String url) {
		Tracks t = client.get().uri(url)
				.headers(httpHeaders -> {
					httpHeaders.setBearerAuth(oauthToken);
				}).retrieve().bodyToMono(Tracks.class).block();
		
		for (Track track : t.fetchAllTracks()) {
			String name = track.getName();
			String artists = "";
			for (Artist artist : track.getArtists()) {
				artists += artist.getName() + ", ";
			}
			artists = artists.substring(0, artists.length() -2);
			System.out.println(name + " - " + artists);
		}
	}
	

}
