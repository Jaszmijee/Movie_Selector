package com.example.movies_selector.controller;

import com.example.movies_selector.domain.MovieDto;
import com.example.movies_selector.exceptions.InvalidRatingException;
import com.example.movies_selector.exceptions.InvalidStatusException;
import com.example.movies_selector.exceptions.MovieNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieControllerTest {

    @Autowired
    MovieController movieController;

    @Nested
    @DisplayName("Test findAllMovies")
    class TestFindAllMovies {

        @DisplayName("findAllMovies - non Empty List")
        @Test
        public void testFindAll() throws MovieNotFoundException {
            //Given
            movieController.addMovie("300");
            movieController.addMovie("Pirates");

            //When
            ResponseEntity<List<MovieDto>> allMoviesResponse = movieController.findAllMovies();

            //Then
            assertThat(allMoviesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals(2, allMoviesResponse.getBody().size());

            //CleanUp
            Long id_300 = allMoviesResponse.getBody().get(0).getMovieId();
            Long id_Pirates = allMoviesResponse.getBody().get(1).getMovieId();
            movieController.deleteMovieById(id_300);
            movieController.deleteMovieById(id_Pirates);
        }

        @DisplayName("findAllMovies - empty List")
        @Test
        public void testFindAllEmpty() {

            //When
            ResponseEntity<List<MovieDto>> llMoviesResponse = movieController.findAllMovies();

            //Then
            assertThat(llMoviesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals(0, llMoviesResponse.getBody().size());
        }
    }

    @Nested
    @DisplayName("Test findMovieById")
    class TestFindMovieById {
        @DisplayName("findMovieById - Id exists in the database")
        @Test
        public void testFindMovieByIdPositiveOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("300");
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();

            ResponseEntity<MovieDto> movieByIdResponse = movieController.findMovieById(id);

            //Then
            assertThat(movieByIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals("300", movieByIdResponse.getBody().getMovieTitle());

            //CleanUp
            movieController.deleteMovieById(id);
        }

        @DisplayName("findMovieById - Id does not exists in the database")
        @Test
        public void testFindMovieByINegativeOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("300");
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieController.findMovieById(-1L));

            //CleanUp
            movieController.deleteMovieById(id);
        }
    }

    @Nested
    @DisplayName("Test findMovieByTitle")
    class FindMovieByTitle {

        @DisplayName("findMovieByTitle - title exists in the database")
        @Test
        public void testFindMovieByTitlePositiveOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");

            //When
            ResponseEntity<MovieDto> movieByIdTitle = movieController.findMovieByTitle("Matrix");

            //Then
            assertThat(movieByIdTitle.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals("Matrix", movieByIdTitle.getBody().getMovieTitle());

            //CleanUp
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();
            movieController.deleteMovieById(id);
        }

        @DisplayName("findMovieByTitle - title does not exists in the database")
        @Test
        public void testFindMovieByTitleNegativeOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");

            //When
            movieController.findMovieByTitle("Matrix");

            //Then
            assertThrows(MovieNotFoundException.class, () -> movieController.findMovieByTitle("Not matrix"));

            //CleanUp
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();
            movieController.deleteMovieById(id);
        }
    }

    @Nested
    @DisplayName("findMoviesByStatus")
    class TestFindMoviesByStatus {
        @DisplayName("findMoviesByStatus - movie exists in the database, status ok")
        @Test
        public void testFindMoviesByStatusPositiveOutput() throws MovieNotFoundException, InvalidStatusException {
            //Given
            movieController.addMovie("Matrix");

            //When
            ResponseEntity<List<MovieDto>> moviesByStatus = movieController.findMoviesByStatus("WAITING_LIST");

            //Then
            assertThat(moviesByStatus.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals(1, moviesByStatus.getBody().size());

            //CleanUp
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();
            movieController.deleteMovieById(id);
        }

        @DisplayName("findMoviesByStatus - movie does not exist in the database, status ok")
        @Test
        public void testFindMoviesByStatusNegativeOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieController.findMoviesByStatus("BAD"));

            //CleanUp
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();
            movieController.deleteMovieById(id);
        }

        @DisplayName("findMoviesByStatus - movie exists in the database, status incorrect")
        @Test
        public void testFindMoviesByStatusNegativeOutputStatusIncorrect() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieController.findMoviesByStatus("aaaaaaa"));

            //CleanUp
            Long id = movieController.findAllMovies().getBody().get(0).getMovieId();
            movieController.deleteMovieById(id);
        }
    }

    @Nested
    @DisplayName("findMoviesByRating")
    class TestFindMoviesByRating {

        @DisplayName("findMoviesByRating - movie exists in the database")
        @Test
        public void testFindMoviesByRatingPositiveOutput() throws MovieNotFoundException, InvalidRatingException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When
            ResponseEntity<List<MovieDto>> moviesAbove7 = movieController.findMoviesByRating("7");
            ResponseEntity<List<MovieDto>> moviesAbove6 = movieController.findMoviesByRating("6.0");

            //Then
            assertThat(moviesAbove7.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(moviesAbove6.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertEquals(2, moviesAbove6.getBody().size());
            assertEquals(1, moviesAbove7.getBody().size());

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }

        @DisplayName("findMoviesByRating - movie does not exist in the database")
        @Test
        public void testFindMoviesByRatingNegativeOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieController.findMoviesByRating("9.0"));

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }
    }

    @Nested
    @DisplayName("addMovie")
    class TestAddMovie {

        @DisplayName("addMovie - movie does not exist in the database")
        @Test
        public void testAddMoviePositiveOutput() throws MovieNotFoundException {

            //Given
            String title = "Matrix";

            //When
            ResponseEntity<Void> movieAdded = movieController.addMovie(title);

            //Then
            assertThat(movieAdded.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertEquals(1, movieController.findAllMovies().getBody().size());

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
        }

        @DisplayName("addMovie - movie exists in the database")
        @Test
        public void testAddMovieNegativeOutput() throws MovieNotFoundException {

            //Given
            String title = "Matrix";

            // When & Then
            movieController.addMovie(title);
            assertThrows(DataIntegrityViolationException.class, () -> movieController.addMovie(title));

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class TestUpdateStatus {

        @DisplayName("updateStatus - movie exists in the database, status ok")
        @Test
        public void testUpdateStatusPositiveOutput() throws MovieNotFoundException, InvalidStatusException {

            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When
            ResponseEntity<Void> movieUpdatedStatus = movieController.updateStatus("Matrix", "GOOD");
            List<MovieDto> listWithStatusGOOD = movieController.findMoviesByStatus("GOOD").getBody();

            //Then
            assertThat(movieUpdatedStatus.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertEquals(1, listWithStatusGOOD.size());

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }

        @DisplayName("updateStatus - movie does not exist in the database, status ok")
        @Test
        public void testAddMovieNegativeOutput() throws MovieNotFoundException {

            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieController.updateStatus("aaaaaa", "GOOD"));

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }

        @DisplayName("updateStatus - movie exists in the database, status incorrect")
        @Test
        public void testAddMovieNegativeOutputStatusIncorrect() throws MovieNotFoundException {

            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieController.updateStatus("Matrix", "aaaaa"));

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }
    }

    @Nested
    @DisplayName("deleteMovieById")
    class TestDeleteMovieById {

        @DisplayName("deleteMovieById - movie exists in the database")
        @Test
        public void testDeleteMovieByIdPositiveOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();

            // When
            ResponseEntity<Void> movieDeletedById = movieController.deleteMovieById(idMatrix);

            //Then
            assertThat(movieDeletedById.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertEquals(1, movieController.findAllMovies().getBody().size());

            //CleanUp
            movieController.deleteMovieById(idEnder);
        }

        @DisplayName("deleteMovieById - movie does not exist in the database")
        @Test
        public void testDeleteMovieByINegativeOutput() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();

            //When & Then
            assertThrows(MovieNotFoundException.class, () -> movieController.deleteMovieById(Long.MAX_VALUE));

            //CleanUp
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }
    }

    @Nested
    @DisplayName("deleteMoviesByStatus")
    class TestDeleteMoviesByStatus {

        @DisplayName("deleteMoviesByStatus - movie exists in the database")
        @Test
        public void testDeleteMoviesByStatusPositiveOutput() throws InvalidStatusException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When
            ResponseEntity<Void> moviesDeletedByStatus = movieController.deleteMoviesByStatus("WAITING_LIST");

            //Then
            assertThat(moviesDeletedByStatus.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertEquals(0, movieController.findAllMovies().getBody().size());
        }

        @DisplayName("deleteMoviesByStatus - movie does not exist in the database")
        @Test
        public void testDeleteMovieByINegativeOutput() throws MovieNotFoundException, InvalidStatusException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");
            ResponseEntity<Void> moviesDeletedByStatus = movieController.deleteMoviesByStatus("BAD");

            //When & Then
            assertThat(moviesDeletedByStatus.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
            assertEquals(2, movieController.findAllMovies().getBody().size());

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }

        @DisplayName("deleteMoviesByStatus - incorrect status")
        @Test
        public void testDeleteMovieByINegativeOutputIncorrectStatus() throws MovieNotFoundException {
            //Given
            movieController.addMovie("Matrix");
            movieController.addMovie("Ender's Game");

            //When & Then
            assertThrows(InvalidStatusException.class, () -> movieController.deleteMoviesByStatus("aaaaaa"));

            //CleanUp
            Long idMatrix = movieController.findMovieByTitle("Matrix").getBody().getMovieId();
            Long idEnder = movieController.findMovieByTitle("Ender's Game").getBody().getMovieId();
            movieController.deleteMovieById(idMatrix);
            movieController.deleteMovieById(idEnder);
        }
    }
}


