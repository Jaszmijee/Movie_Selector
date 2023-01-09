package com.example.movies_selector.mapper;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.MovieDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoviesMapper {

    public MovieDto mapToMovieDto(Movie movie) {
        return new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getYear(),
                movie.getDuration(),
                movie.getStatus().toString(),
                movie.getImdbStatus()
        );
    }

    public List<MovieDto> mapToMovieDtoList(List<Movie> movies) {
        return movies.stream().
                map(this::mapToMovieDto).
                collect(Collectors.toList());
    }
    }
