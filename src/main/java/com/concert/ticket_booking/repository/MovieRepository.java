package com.concert.ticket_booking.repository;

import com.concert.ticket_booking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitle(String title);
    Optional<Movie> findByLanguage(String title);
    List<Movie> findByGenre(String genre);
}
