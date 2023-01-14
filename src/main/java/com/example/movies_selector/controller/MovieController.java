package com.example.movies_selector.controller;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.MovieDto;
import com.example.movies_selector.exceptions.InvalidRatingException;
import com.example.movies_selector.exceptions.InvalidStatusException;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import com.example.movies_selector.mapper.MoviesMapper;
import com.example.movies_selector.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "movies")
public class MovieController {

    private final MovieService movieService;

    private final MoviesMapper mapper;

    @Autowired
    public MovieController(MovieService movieService, MoviesMapper mapper) {
        this.movieService = movieService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<MovieDto>> findAllMovies() {
        List<Movie> listOfAll = movieService.findAll();
        return ResponseEntity.ok(mapper.mapToMovieDtoList(listOfAll));
    }

    @GetMapping(value = "/Id/{movieId}")
    public ResponseEntity<MovieDto> findMovieById(@PathVariable Long movieId) throws MovieNotFoundException {
        Movie movie = movieService.findById(movieId);
        return ResponseEntity.ok(mapper.mapToMovieDto(movie));
    }

    @GetMapping(value = "/title/{title}")
    public ResponseEntity<MovieDto> findMovieByTitle(@PathVariable String title) throws MovieNotFoundException {
        Movie movieWithTitle = movieService.findByTitle(title);
        return ResponseEntity.ok(mapper.mapToMovieDto(movieWithTitle));
    }

    @GetMapping(value = "/status")
    public ResponseEntity<List<MovieDto>> findMoviesByStatus(@RequestParam String status) throws MovieNotFoundException, InvalidStatusException {
        List<Movie> moviesByStatus = movieService.findByStatus(status);
        return ResponseEntity.ok(mapper.mapToMovieDtoList(moviesByStatus));
    }

    @GetMapping(value = "/rating")
    public ResponseEntity<List<MovieDto>> findMoviesByRating(@RequestParam String rating) throws MovieNotFoundException, InvalidRatingException {
        List<Movie> moviesBetterThan = movieService.findByRating(rating);
        return ResponseEntity.ok(mapper.mapToMovieDtoList(moviesBetterThan));
    }

    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestParam String title) {
        movieService.saveByTitle(title);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateStatus(@RequestParam String title, @RequestParam String status) throws MovieNotFoundException, InvalidStatusException {
        movieService.updateStatus(title, status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping(value = "{movieId}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable Long movieId) throws MovieNotFoundException {
        movieService.deleteById(movieId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteMoviesByStatus(@RequestParam String status) throws InvalidStatusException {
        movieService.deleteAllByStatus(status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}





