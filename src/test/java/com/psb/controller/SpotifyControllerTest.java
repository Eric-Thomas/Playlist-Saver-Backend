package com.psb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.psb.client.AWSS3Client;
import com.psb.client.SpotifyClient;
import com.psb.exception.SpotifyClientException;
import com.psb.exception.SpotifyClientUnauthorizedException;
import com.psb.model.spotify.SpotifyPlaylist;
import com.psb.testUtils.SpotifyUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {SpotifyController.class})
class SpotifyControllerTest {

    @LocalServerPort
    private static int port;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpotifyClient spotifyClient;
    @MockBean
    private AWSS3Client s3Client;

    private SpotifyUtil spotifyUtil = new SpotifyUtil();
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String OAUTH = "oauthToken";
    private static final String ERROR_MESSAGE = "Test error message";
    private static final String SAVE_PLAYLISTS_URL = "/spotify/playlists/save";

    @BeforeEach
    public void initialize() {
        spotifyUtil.setMockServerUrl(String.format("http://localhost:%s", port));
    }


    @Test
    void testInvalidOauthToken() throws Exception {
        when(spotifyClient.getUser(Mockito.anyString()))
                .thenThrow(new SpotifyClientUnauthorizedException(ERROR_MESSAGE));
        List<SpotifyPlaylist> testPlaylists = new ArrayList<>();
        testPlaylists.add(spotifyUtil.createTestPlaylist());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SAVE_PLAYLISTS_URL).contentType(MediaType.APPLICATION_JSON)
                        .header("OAUTHToken", OAUTH).content(objectMapper.writeValueAsString(testPlaylists)))
                .andExpect(status().isUnauthorized()).andExpect(content().string(containsString(ERROR_MESSAGE)));
    }

    @Test
    void testNoContentTypeHeader() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(SAVE_PLAYLISTS_URL)).andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidBody() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post(SAVE_PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void testSavePlaylistsError() throws Exception {
        when(spotifyClient.getUser(Mockito.anyString())).thenThrow(new SpotifyClientException(ERROR_MESSAGE));
        List<SpotifyPlaylist> testPlaylists = new ArrayList<>();
        testPlaylists.add(spotifyUtil.createTestPlaylist());
        this.mockMvc
                .perform(MockMvcRequestBuilders.post(SAVE_PLAYLISTS_URL).accept(MediaType.APPLICATION_JSON)
                        .header("OAUTHToken", OAUTH).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPlaylists)))
                .andExpect(status().isServiceUnavailable()).andExpect(content().string(containsString(ERROR_MESSAGE)));
    }

}
