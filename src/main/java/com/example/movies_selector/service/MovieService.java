package com.example.movies_selector.service;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.Status;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import com.example.movies_selector.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    @Autowired
    MovieRepository movieRepository;

    public List<Movie> findAll() {
        return movieRepository.findAll();
    }

    public Movie findById(Long movieId) throws MovieNotFoundException {
        return movieRepository.findById(movieId).orElseThrow(MovieNotFoundException::new);
    }

    public Movie findByTitle(String title) {
        return movieRepository.findByTitleEquals(title);
    }

    public List<Movie> findByStatus(Status status) {
        return movieRepository.findByStatus(status);
    }

    public Movie save(Movie movie){
        return movieRepository.save(movie);
    }

    public List<Movie> findByRating(String rating) {
        Double expectedRating = Double.parseDouble(rating);
        return findAll().stream()
                .filter(ratings -> Double.parseDouble(ratings.getImdbStatus()) >= expectedRating)
                .collect(Collectors.toList());
    }

    public void deleteById(Long movieId) throws MovieNotFoundException {
        findById(movieId);
        movieRepository.deleteById(movieId);
    }

    public void deleteAllByStatus(Status status){
        movieRepository.deleteAllByStatus(status);
    }
}

