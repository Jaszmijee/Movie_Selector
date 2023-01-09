package com.example.movies_selector.domain;


import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "MOVIES", uniqueConstraints = {@UniqueConstraint(columnNames = {"TITLE", "PRODUCTION_YEAR" })})
public class Movie {

    private Long id;
    private String title;
    private int year;
    private Status status;
    private String imdbStatus;
    String duration;

    public Movie(String title) {
        this.title = title;
    }

    public Movie() {
    }
    @Id
    @GeneratedValue
    @Column(name = "ID")
    public Long getMovieId() {
        return id;
    }

    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    @Column(name = "PRODUCTION_YEAR")
    public int getYear() {
        return year;
    }

    @Column(name = "DURATION")
    public String getDuration() {
        return duration;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MY_STATUS")
    public Status getStatus() {
        return status;
    }

    @Column(name = "IMDB_RATING")
    public String getImdbStatus() {
        return imdbStatus;
    }

    private void setMovieId(Long movieId) {
        this.id = movieId;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setImdbStatus(String imdbStatus) {
        this.imdbStatus = imdbStatus;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
