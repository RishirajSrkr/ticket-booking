package com.concert.ticket_booking.repository;

import com.concert.ticket_booking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {


    // Find events happening on a specific date (not exact time)
    List<Show> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find upcoming events
    List<Show> findByDateTimeAfter(LocalDateTime now);

}
