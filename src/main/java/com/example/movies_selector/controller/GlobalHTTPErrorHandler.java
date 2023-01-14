package com.example.movies_selector.controller;

import com.example.movies_selector.exceptions.InvalidRatingException;
import com.example.movies_selector.exceptions.InvalidStatusException;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalHTTPErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<Object> handleMovieNotFound() {
        return new ResponseEntity<>("Movie not found", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDuplicateEntry() {
        return new ResponseEntity<>("Movie already exists", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<Object> handleImproperRatingParameter(){
        return new ResponseEntity<>("Provide rating in range 0.0 to 10.0", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<Object> handleInvalidStatusParameter(){
        return new ResponseEntity<>("Provide correct status, one of: VERY_BAD, BAD, GOOD,VERY_GOOD, WAITING_LIST", HttpStatus.BAD_REQUEST);
    }
}
