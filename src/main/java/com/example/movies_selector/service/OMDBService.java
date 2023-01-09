package com.example.movies_selector.service;

import com.example.movies_selector.domain.MovieInfoOMDBDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class OMDBService {

    @Value("${apikey}")
    private String apiKey;

    private URI omdbUri(String title) {
        return UriComponentsBuilder.fromHttpUrl("http://www.omdbapi.com/")
                .queryParam("apikey", apiKey)
                .queryParam("t", title)
                .encode().build().toUri();
    }

    public MovieInfoOMDBDto getMovieFromOMDB(String title) {
        RestTemplate restTemplate = new RestTemplate();
        URI url = omdbUri(title);
        return restTemplate.getForObject(url, MovieInfoOMDBDto.class);
    }
}

