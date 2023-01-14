package com.example.movies_selector.service;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.MovieInfoOMDBDto;
import com.example.movies_selector.domain.Status;
import com.example.movies_selector.exceptions.InvalidRatingException;
import com.example.movies_selector.exceptions.InvalidStatusException;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import com.example.movies_selector.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    MovieRepository movieRepository;

    OMDBService omdbService;

    @Autowired
    public MovieService(MovieRepository movieRepository, OMDBService omdbService) {
        this.movieRepository = movieRepository;
        this.omdbService = omdbService;
    }

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie findById(Long movieId) throws MovieNotFoundException {
        return movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
    }

    public Movie findByTitle(String title) throws MovieNotFoundException {
        Movie moviesByTitle = movieRepository.findByTitleEqualsIgnoreCase(title);
        if (moviesByTitle == null) {
            throw new MovieNotFoundException();
        }
        return moviesByTitle;
    }

    public Status checkIfStatusCorrect(String status) throws InvalidStatusException {

        return switch (status) {
            case "BAD", "GOOD", "VERY_BAD", "VERY_GOOD", "WAITING_LIST" -> Status.valueOf(status);
            default -> throw new InvalidStatusException();
        };
    }

    public List<Movie> findByStatus(String status) throws MovieNotFoundException, InvalidStatusException {

        List<Movie> moviesByStatus = movieRepository.findByStatus(checkIfStatusCorrect(status));
        if (moviesByStatus.isEmpty()) {
            throw new MovieNotFoundException();
        }
        return moviesByStatus;
    }

    public List<Movie> findByRating(String rating) throws MovieNotFoundException, InvalidRatingException {
        try {
            double expectedRating = Double.parseDouble(rating);
            if (expectedRating < 0 || expectedRating > 10) {
                throw new InvalidRatingException();
            }
        } catch (NumberFormatException e) {
            throw new InvalidRatingException();
        }

        List<Movie> moviesBetterThan = movieRepository.findAll().stream()
                .filter(r -> r.getImdbStatus() != null)
                .filter(ratings -> Double.parseDouble(ratings.getImdbStatus()) >= Double.parseDouble(rating))
                .collect(Collectors.toList());
        if (moviesBetterThan.isEmpty()) {
            throw new MovieNotFoundException();
        }
        return moviesBetterThan;
    }

    public Movie save(Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie saveByTitle(String title) {
        MovieInfoOMDBDto movieFromOMDB = omdbService.getMovieFromOMDB(title);
        Movie newMovie;
        if (movieFromOMDB.getResponse().equals("False")) {
            newMovie = new Movie(title);
        } else {
            newMovie = new Movie(movieFromOMDB.getTitle());
            newMovie.setYear(movieFromOMDB.getYear() == null || (movieFromOMDB.getYear().equals("N/A")) ? "0" : movieFromOMDB.getYear());
            newMovie.setImdbStatus((movieFromOMDB.getImdbStatus() == null || movieFromOMDB.getImdbStatus().equalsIgnoreCase("N/A")) ? "0" : movieFromOMDB.getImdbStatus());
            newMovie.setDuration((movieFromOMDB.getDuration() == null || (movieFromOMDB.getDuration().equals("N/A")) ? null : movieFromOMDB.getDuration()));
        }
        newMovie.setStatus(Status.WAITING_LIST);
        save(newMovie);
        return newMovie;
    }

    public Movie updateStatus(String title, String status) throws MovieNotFoundException, InvalidStatusException {
        Movie movieToUpdate = findByTitle(title);
        movieToUpdate.setStatus(checkIfStatusCorrect(status));
        return save(movieToUpdate);
    }

    public void deleteById(Long movieId) throws MovieNotFoundException {
        findById(movieId);
        movieRepository.deleteById(movieId);
    }

    public void deleteAllByStatus(String status) throws InvalidStatusException {
        movieRepository.deleteAllByStatus(checkIfStatusCorrect(status));
    }
}

