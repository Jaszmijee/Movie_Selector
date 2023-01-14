package com.example.movies_selector.repository;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface MovieRepository extends CrudRepository<Movie, Long> {

    @Override
    List<Movie> findAll();

    @Override
    Optional<Movie> findById(Long aLong);

    @Override
    Movie save(Movie movie);

    Movie findByTitleEqualsIgnoreCase(String title);

    List<Movie> findByStatus(Status status);

    @Override
    void deleteById(Long movieId);

    void deleteAllByStatus(Status status);
}



