package com.example.movies_selector.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovieDto {
    Long movieId;
    String movieTitle;
    String productionYear;
    String duration;
    String status;
    String rating;
}
