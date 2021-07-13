package com.psb.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.psb.client.AWSS3Client;
import com.psb.exception.AWSS3ClientException;
import com.psb.exception.AWSS3ClientNotFoundException;
import com.psb.model.s3.S3Playlist;

@RestController
@RequestMapping("/s3")
public class S3Controller {

	private AWSS3Client s3Client;

	@Autowired
	public S3Controller(AWSS3Client s3Client) {
		this.s3Client = s3Client;
	}
	
	@GetMapping(path = "/load/users/{id}/playlists")
	public List<S3Playlist> loadPlaylists(@PathVariable String id) throws AWSS3ClientException, AWSS3ClientNotFoundException {
		return s3Client.getPlaylists(id);
		
	}
	
	@GetMapping(path = "/load/users")
	public Map<String, String> loadUsers() throws AWSS3ClientException {
		return s3Client.getAllUsers();
	}

}
