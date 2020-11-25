package com.psb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class SpotifyService {
	
	private final WebClient client;
	
	@Value("${spotify.base.url}")
	private String spotifyBaseUrl;
	
	public SpotifyService(WebClient.Builder webClientBuilder) {
		this.client = webClientBuilder.baseUrl(spotifyBaseUrl).build();
	}

}
