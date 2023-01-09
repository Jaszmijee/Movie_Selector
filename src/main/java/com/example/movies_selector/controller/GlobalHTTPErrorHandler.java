package com.example.movies_selector.controller;

import com.example.movies_selector.exceptions.MovieNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class GlobalHTTPErrorHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Object> handleMovieNotFound() throws MovieNotFoundException {
        return new ResponseEntity("Movie not found", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> handleDuplicateMovie() throws SQLIntegrityConstraintViolationException {
        return new ResponseEntity("Movie already exists", HttpStatus.BAD_REQUEST);
    }
}
