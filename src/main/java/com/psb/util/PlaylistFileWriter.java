package com.psb.util;

import java.util.List;

import com.psb.model.repository.Playlist;

public class PlaylistFileWriter {
	
	public static void writePlaylistsToFile(List<Playlist> playlists) {
		for (Playlist playlist: playlists) {
			System.out.println("Writing " + playlist.getPlaylistName() + " to file");
		}
		
	}

}
