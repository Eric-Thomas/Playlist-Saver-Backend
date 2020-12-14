package com.psb.util;

import java.util.List;

import com.psb.model.response.PlaylistResponse;

public class PlaylistFileWriter {
	
	public static void writePlaylistsToFile(List<PlaylistResponse> playlists) {
		for (PlaylistResponse playlist: playlists) {
			System.out.println("Writing " + playlist.getPlaylistName() + " to file");
		}
		
	}

}
