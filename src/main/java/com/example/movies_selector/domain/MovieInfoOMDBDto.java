package com.example.movies_selector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieInfoOMDBDto {

    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private String year;
    @JsonProperty("imdbRating")
    private String imdbStatus;
    @JsonProperty("Runtime")
    private String duration;
    @JsonProperty("Response")
    private String response;


}
