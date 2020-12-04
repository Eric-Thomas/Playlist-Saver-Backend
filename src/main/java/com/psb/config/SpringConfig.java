package com.psb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SpringConfig {
	
	@Value("${spotify.base.url}")
	private String spotifyBaseUrl;
	
	@Bean
	public WebClient getWebClientBuilder(){
	    return   WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
	            .codecs(configurer -> configurer
	                      .defaultCodecs()
	                      .maxInMemorySize(16 * 1024 * 1024))
	                    .build())
	    		.baseUrl(spotifyBaseUrl)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
	                  .build();
	}
}
