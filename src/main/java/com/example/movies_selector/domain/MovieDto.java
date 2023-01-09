package com.example.movies_selector.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class MovieDto {
    Long movieId;
    String movieTitle;
    int productionYear;
    String duration;
    String status;
    String rating;
}
