package com.psb.config;

import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.profiles.ProfileFile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class SpringConfig {

	@Value("${spotify.base.url}")
	private String spotifyBaseUrl;

	@Bean
	public WebClient getWebClientBuilder() {
		return WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
				.baseUrl(spotifyBaseUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.clientConnector(new ReactorClientHttpConnector(HttpClient.create().followRedirect(true))).build();
	}

	@Bean
	public S3Client getS3ClientBuilder() {
		ProfileFile profileFile = ProfileFile.builder().content(Paths.get("credentials"))
				.type(ProfileFile.Type.CREDENTIALS).build();
		ProfileCredentialsProvider provider = ProfileCredentialsProvider.builder().profileFile(profileFile).build();
		Region region = Region.US_EAST_1;
		return S3Client.builder().credentialsProvider(provider).region(region).build();
	}
}
