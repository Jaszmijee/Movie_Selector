package com.example.movies_selector.service;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.Status;
import com.example.movies_selector.exceptions.InvalidRatingException;
import com.example.movies_selector.exceptions.InvalidStatusException;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import com.example.movies_selector.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MovieServiceTest {

    @Autowired
    private MovieService movieService;

    @Autowired
    MovieRepository movieRepository;


    @Nested
    @DisplayName("Test findAll")
    class TestFindAll {

        @DisplayName("findAll - movie exists in the database")
        @Test
        void testFindAllPositiveOutput() {

            //Given
            movieService.save(new Movie("Matrix"));
            movieService.save(new Movie("Ender's Game"));

            //When
            List<Movie> allMovies = movieService.findAll();

            //Then
            assertEquals(2, allMovies.size());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findAll - movie does not exist in the database")
        @Test
        void testFindAllNegativeOutput() {

            //When
            List<Movie> allMovies = movieService.findAll();

            //When & Then
            assertEquals(0, allMovies.size());
        }
    }

    @Nested
    @DisplayName("Test findById")
    class TestFindById {

        @DisplayName("findById - movie exists in the database")
        @Test
        void testFindByIdPositiveOutput() throws MovieNotFoundException {
            //Given
            movieService.saveByTitle("Matrix");
            Long idMatrix = movieService.findAll().get(0).getMovieId();

            //When & Then
            assertEquals("Matrix", movieService.findById(idMatrix).getTitle());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findById - movie does not exist in the database")
        @Test
        void testFindByIdNegativeOutput() {
            //Given
            movieService.saveByTitle("Matrix");
            Long idMatrix = movieService.findAll().get(0).getMovieId();

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieService.findById(idMatrix + 1));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test findByTitle")
    class TestFindByTitle {

        @DisplayName("findByTitle - movie exists in the database")
        @Test
        void testFindByTitlePositiveOutput() throws MovieNotFoundException {
            //Given
            movieService.saveByTitle("Matrix");

            //When & Then
            assertEquals("Matrix", movieService.findByTitle("mAtrIX").getTitle());
            assertDoesNotThrow(() -> movieService.findByTitle("Matrix"));

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByTitle - movie does not exist in the database")
        @Test
        void testFindByTitleNegativeOutput() {
            //Given
            movieService.saveByTitle("Matrix");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieService.findByTitle("Not - Matrix"));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test checkIfStatusCorrect")
    class TestCheckIfStatusCorrect {

        @DisplayName("checkIfStatusCorrect - Status ok")
        @Test
        void testCheckIfStatusCorrectPositiveOutput() throws InvalidStatusException {
            //Given
            String VERY_BAD = "VERY_BAD";
            String BAD = "BAD";
            String GOOD = "GOOD";
            String VERY_GOOD = "VERY_GOOD";
            String WAITING_LIST = "WAITING_LIST";

            //When & Then
            assertEquals(Status.VERY_BAD, movieService.checkIfStatusCorrect(VERY_BAD));
            assertEquals(Status.BAD, movieService.checkIfStatusCorrect(BAD));
            assertEquals(Status.GOOD, movieService.checkIfStatusCorrect(GOOD));
            assertEquals(Status.VERY_GOOD, movieService.checkIfStatusCorrect(VERY_GOOD));
            assertEquals(Status.WAITING_LIST, movieService.checkIfStatusCorrect(WAITING_LIST));
        }

        @DisplayName("checkIfStatusCorrect - Status incorrect")
        @Test
        void testCheckIfStatusCorrectNegativeOutput() {
            //Given
            String incorrect = "aaaaaa";

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieService.checkIfStatusCorrect(incorrect));
        }
    }

    @Nested
    @DisplayName("Test findByStatus")
    class TestFindByStatus {

        @DisplayName("findByStatus - movie exists in the database")
        @Test
        void testFindByStatusPositiveOutput() throws MovieNotFoundException, InvalidStatusException {
            //Given

            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertEquals(2, movieService.findByStatus("WAITING_LIST").size());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByStatus - movie does not exist in the database")
        @Test
        void testFindByStatusNegativeOutput() {
            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieService.findByStatus("GOOD"));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test findByRating")
    class TestFindByRating {

        @DisplayName("findByRating - movie exists in the database, rating ok")
        @Test
        void testFindByRatingPositiveOutput() throws MovieNotFoundException, InvalidRatingException {

            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertEquals(2, movieService.findByRating("0").size());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByRating - movie does not exist in the database, rating ok")
        @Test
        void testFindByRatingNegativeOutput() {

            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieService.findByRating("9"));

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByRating - rating not a number")
        @Test
        void testFindByRatingNegativeOutputRatingNotNumber() {

            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertThrows(InvalidRatingException.class, () -> movieService.findByRating("aaaaa"));

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByRating - rating not in Range")
        @Test
        void testFindByRatingNegativeOutputRatingNotInRange() {

            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertThrows(InvalidRatingException.class, () -> movieService.findByRating("-1"));
            assertThrows(InvalidRatingException.class, () -> movieService.findByRating("10.1"));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test save")
    class TestSave {

        @DisplayName("save - movie exists in the database")
        @Test
        void savePositiveOutput() {
            //Given
            Movie matrix = new Movie("Matrix");

            //When
            Movie savedMovie = movieService.save(matrix);

            //Then
            assertEquals("Matrix", savedMovie.getTitle());
            assertNull(savedMovie.getImdbStatus());
            assertNull(savedMovie.getYear());
            assertNull(savedMovie.getDuration());
            assertNull(savedMovie.getStatus());

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test saveByTitle")
    class TestSaveByTitle {

        @DisplayName("findByRating - movie exists in the OMDB database")
        @Test
        void saveByTitleExistsInOmdb() {
            //Given
            String title = "Matrix";

            //When
            Movie savedMovie = movieService.saveByTitle(title);

            //Then
            assertEquals("Matrix", savedMovie.getTitle());
            assertEquals("7.7", savedMovie.getImdbStatus());
            assertEquals("1993", savedMovie.getYear());
            assertEquals("60 min", savedMovie.getDuration());
            assertEquals(Status.WAITING_LIST, savedMovie.getStatus());

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @DisplayName("findByRating - movie does not exists in the OMDB database")
    @Test
    void saveByTitleNotExistsInOmdb() {
        //Given
        String title = "CatRunThroughKeyboard";

        //When
        Movie savedMovie = movieService.saveByTitle(title);

        //Then
        assertEquals("CatRunThroughKeyboard", savedMovie.getTitle());
        assertNull(savedMovie.getImdbStatus());
        assertNull(savedMovie.getYear());
        assertNull(savedMovie.getDuration());
        assertEquals(Status.WAITING_LIST, savedMovie.getStatus());

        //ClenUp
        movieRepository.deleteAll();
    }

    @Nested
    @DisplayName("Test updateStatus")
    class TestUpdateStatus {

        @DisplayName("updateStatus - movie exists in the database, status ok")
        @Test
        void updateStatusPositiveOutput() throws MovieNotFoundException, InvalidStatusException {

            //Given
            movieService.saveByTitle("Matrix");

            //When
            Movie movie = movieService.updateStatus("Matrix", "GOOD");

            //Then
            assertEquals(Status.GOOD, movie.getStatus());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("updateStatus - movie does not exists in the database, status ok")
        @Test
        void updateStatusNegativeOutput() {

            //Given
            movieService.saveByTitle("Matrix");

            //Then
            assertThrows(MovieNotFoundException.class, () -> movieService.updateStatus("Not - Matrix", "GOOD"));

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("updateStatus - movie exists in the database, status incorrect")
        @Test
        void updateStatusNegativeOutputIncorrectStatus() {

            //Given
            movieService.saveByTitle("Matrix");

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieService.updateStatus("Matrix", "aaaaaaaaa"));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test deleteById")
    class TestDeleteById {

        @DisplayName("deleteById - movie exists in the database")
        @Test
        void deleteByIdPositiveOutput() throws MovieNotFoundException {

            //Given
            Movie movie = movieService.saveByTitle("Matrix");
            Long movieId = movie.getMovieId();

            //When
            movieService.deleteById(movieId);

            //Then
            assertEquals(0, movieService.findAll().size());
        }

        @DisplayName("deleteById - movie does not exist in the database")
        @Test
        void deleteByIdNegativeOutput() {

            //Given
            Movie movie = movieService.saveByTitle("Matrix");
            Long movieId = movie.getMovieId();

            //When
            assertThrows(MovieNotFoundException.class, () -> movieService.deleteById(movieId + 44));

            //ClenUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test deleteAllByStatus")
    class TestDeleteAllByStatusPositiveOutput {

        @DisplayName("deleteAllByStatus - movie exists in the database, status ok")
        @Test
        void deleteAllByStatusPositiveOutput() throws InvalidStatusException {
            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When
            movieService.deleteAllByStatus("WAITING_LIST");

            //Then
            assertEquals(0, movieService.findAll().size());
        }

        @DisplayName("deleteAllByStatus - movie does not exists in the database, status ok")
        @Test
        void deleteAllByStatusNegativeOutput() throws InvalidStatusException {
            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When
            movieService.deleteAllByStatus("GOOD");

            //Then
            assertEquals(2, movieService.findAll().size());

            //ClenUp
            movieRepository.deleteAll();
        }

        @DisplayName("deleteAllByStatus - movie does not exists in the database, status ok")
        @Test
        void deleteAllByStatusNegativeOutputStatusIncorrect() {
            //Given
            movieService.saveByTitle("Matrix");
            movieService.saveByTitle("Ender's Game");

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieService.deleteAllByStatus("aaaaaa"));

            //ClenUp
            movieRepository.deleteAll();
        }
    }
}