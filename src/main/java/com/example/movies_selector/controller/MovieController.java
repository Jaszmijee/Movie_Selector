package com.example.movies_selector.controller;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.MovieDto;
import com.example.movies_selector.domain.MovieInfoOMDBDto;
import com.example.movies_selector.domain.Status;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import com.example.movies_selector.mapper.MoviesMapper;
import com.example.movies_selector.service.MovieService;
import com.example.movies_selector.service.OMDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private MoviesMapper mapper;

    @Autowired
    private OMDBService omdbService;

    public MovieController(MovieService movieService, MoviesMapper mapper, OMDBService omdbService) {
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
    public ResponseEntity<List<MovieDto>> findMoviesByStatus(@RequestParam Status status) throws MovieNotFoundException {
        List<Movie> moviesByStatus = movieService.findByStatus(status);
        if (moviesByStatus.isEmpty()) {
            throw new MovieNotFoundException();
        }
        return ResponseEntity.ok(mapper.mapToMovieDtoList(moviesByStatus));
    }

    @GetMapping(value = "/rating")
    public ResponseEntity<List<MovieDto>> findMoviesByRating(@RequestParam String rating) throws MovieNotFoundException {
        List<Movie> moviesBetterThan = movieService.findByRating(rating);
        if (moviesBetterThan.isEmpty()) {
            throw new MovieNotFoundException();
        } else return ResponseEntity.ok(mapper.mapToMovieDtoList(moviesBetterThan));
    }

    @PostMapping
    public ResponseEntity<Void> addMovie(@RequestParam String title) {
        MovieInfoOMDBDto movieFromOMDB = omdbService.getMovieFromOMDB(title);
        Movie newMovie = new Movie(title);
        newMovie.setStatus(Status.WAITING_LIST);
        if (movieFromOMDB.getResponse().equals("False")) {
            movieService.save(newMovie);
        } else {
            newMovie.setYear((movieFromOMDB.getYear() == "") ? 0 : Integer.parseInt(movieFromOMDB.getYear()));
            newMovie.setImdbStatus((movieFromOMDB.getYear() == null || movieFromOMDB.getYear() == "N/A") ? "0" : movieFromOMDB.getImdbStatus());
            newMovie.setDuration((movieFromOMDB.getDuration() == null || (movieFromOMDB.getDuration() == "") ? null : movieFromOMDB.getDuration()));
            movieService.save(newMovie);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateStatus(@RequestParam String title, @RequestParam Status status) throws MovieNotFoundException {
        Movie movieToUpdate = movieService.findByTitle(title);
        movieToUpdate.setStatus(status);
        movieService.save(movieToUpdate);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping(value = "{movieId}")
    public ResponseEntity<Void> deleteMovieById(@PathVariable Long movieId) throws MovieNotFoundException {
        movieService.deleteById(movieId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteMoviesByStatus(@RequestParam Status status) throws MovieNotFoundException {
        movieService.deleteAllByStatus(status);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}





