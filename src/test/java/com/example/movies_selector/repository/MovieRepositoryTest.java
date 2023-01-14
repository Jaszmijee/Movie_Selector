package com.example.movies_selector.repository;

import com.example.movies_selector.domain.Movie;
import com.example.movies_selector.domain.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MovieRepositoryTest {

    @Autowired
    MovieRepository movieRepository;

    @Nested
    @DisplayName("Test FindALL")
    class TestFindALL {
        @DisplayName("findAll() - non Empty List")
        @Test
        public void testFindAll() {
            //Given
            Movie movie1 = new Movie("300");
            Movie movie2 = new Movie(("400"));

            movieRepository.save(movie1);
            movieRepository.save(movie2);

            //When
            List<Movie> moviesList = movieRepository.findAll();

            //Then
            assertEquals(2, moviesList.size());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("findAll() - empty List")
        @Test
        public void testFindAllEmpty() {
            //When
            List<Movie> moviesList = movieRepository.findAll();

            //Then
            assertEquals(0, moviesList.size());

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test findById")
    class TestFindById {

        @DisplayName("findById() - Id exists in the database")
        @Test
        public void testFindByIdExists() {
            // Given
            Movie movie1 = new Movie("300");
            movieRepository.save(movie1);
            Long id = movie1.getMovieId();

            //When & Then
            assertTrue(movieRepository.findById(id).isPresent());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("findById() -Id does not exist in the database")
        @Test
        public void testFindByIdDoesNotExist() {
            // Given
            Movie movie1 = new Movie("300");
            movieRepository.save(movie1);
            Long id = movie1.getMovieId() + 1;

            //When & Then
            assertTrue(movieRepository.findById(id).isEmpty());

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test save")
    public class TestSave {

        @DisplayName("save() - movie not present in the database")
        @Test
        public void testSavePositiveOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Movie movie2 = new Movie(("400"));

            //When
            movieRepository.save(movie1);
            movieRepository.save(movie2);
            List<Movie> list = movieRepository.findAll();
            String expectedTitle = movie1.getTitle();
            List<String> resultTitles = list.stream().map(m -> m.getTitle()).toList();

            //Then
            assertFalse(list.isEmpty());
            assertTrue(resultTitles.contains(expectedTitle));

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("save() - movie already present in the database")
        @Test
        public void testSaveNegativeOutput() {
            //Given
            Movie movie1 = new Movie("300");
            movie1.setYear("0");
            Movie movie2 = new Movie("400");
            movie2.setYear("0");
            Movie movie3 = new Movie("400");
            movie3.setYear("0");

            //When
            movieRepository.save(movie1);
            movieRepository.save(movie2);

            //Then
            assertThrows(DataIntegrityViolationException.class, () -> movieRepository.save(movie3));

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test findByTitleEqualsIgnoreCase")
    class TestFindByTitleEqualsIgnoreCase {

        @DisplayName("findByTitleEqualsIgnoreCase() - title present in the database")
        @Test
        public void testFindByTitleEqualsIgnoreCasePositiveOutput() {
            //Give
            Movie movie1 = new Movie("300");
            movieRepository.save(movie1);

            //When
            Movie resultMovie = movieRepository.findByTitleEqualsIgnoreCase("300");

            //Then
            assertEquals("300", resultMovie.getTitle());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByTitleEqualsIgnoreCase() - title not present in the database")
        @Test
        public void testFindByTitleEqualsIgnoreCaseNegativeOutput() {
            //Give
            Movie movie1 = new Movie("300");
            movieRepository.save(movie1);

            //Then
            assertNull(movieRepository.findByTitleEqualsIgnoreCase("matrix"));

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test findByStatus")
    class TestFindByStatus {
        @DisplayName("findByStatus() - status present in the database")
        @Test
        public void testFindByStatusPositiveOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Status status = Status.WAITING_LIST;
            movie1.setStatus(status);
            movieRepository.save(movie1);

            // When & Then
            assertEquals(1, movieRepository.findByStatus(status).size());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("findByStatus() - status not present in the database")
        @Test
        public void testFindByStatusNegativeOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Status status = Status.WAITING_LIST;
            movie1.setStatus(status);
            movieRepository.save(movie1);

            //When & Then
            assertEquals(0, movieRepository.findByStatus(Status.BAD).size());

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test deleteById")
    class TestDeleteById {

        @DisplayName("deleteById() - Id present in the database")
        @Test
        public void testDeleteByIdPositiveOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Movie movie2 = new Movie(("400"));
            movieRepository.save(movie1);
            movieRepository.save(movie2);
            Long id1 = movie1.getMovieId();

            //When
            movieRepository.deleteById(id1);

            //Then
            assertEquals(1, movieRepository.findAll().size());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("deleteById() - Id not present in the database")
        @Test
        public void testDeleteByIdNegativeOutput() {
            //Given
            Movie movie1 = new Movie("1000");
            Movie movie2 = new Movie(("4800"));
            movieRepository.save(movie1);
            movieRepository.save(movie2);
            int size = movieRepository.findAll().size();

            //Then
            try {
                movieRepository.deleteById(Long.MAX_VALUE);
                assertEquals(size, movieRepository.findAll().size());
            } catch (Exception e) {
                System.out.println("Movie with this Id does not exist");
            }

            //CleanUp
            movieRepository.deleteAll();
        }
    }

    @Nested
    @DisplayName("Test deleteAllByStatus")
    class TestDeleteAllByStatus {

        @DisplayName("deleteAllByStatus() - movie with this status exists in the database")
        @Test
        public void testDeleteAllByStatusPositiveOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Movie movie2 = new Movie(("400"));
            movie1.setStatus(Status.BAD);
            movie2.setStatus(Status.BAD);
            movieRepository.save(movie1);
            movieRepository.save(movie2);

            //When
            movieRepository.deleteAllByStatus(Status.BAD);

            //Then
            assertEquals(0, movieRepository.findAll().size());

            //CleanUp
            movieRepository.deleteAll();
        }

        @DisplayName("deleteAllByStatus() - movie with this status does not exist in the database")
        @Test
        public void testDeleteAllByStatusNegativeOutput() {
            //Given
            Movie movie1 = new Movie("300");
            Movie movie2 = new Movie(("400"));
            movie1.setStatus(Status.BAD);
            movie2.setStatus(Status.BAD);
            movieRepository.save(movie1);
            movieRepository.save(movie2);

            //When
            movieRepository.deleteAllByStatus(Status.WAITING_LIST);

            //Then
            assertEquals(2, movieRepository.findAll().size());

            //CleanUp
            movieRepository.deleteAll();
        }
    }
}



