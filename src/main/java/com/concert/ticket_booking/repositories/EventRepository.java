package com.concert.ticket_booking.repositories;

import com.concert.ticket_booking.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event>findByName(String eventName);

    // Find events happening on a specific date (not exact time)
    List<Event> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Find events at a location
    List<Event> findByLocation(String location);

    // Find upcoming events
    List<Event> findByDateTimeAfter(LocalDateTime now);

}
