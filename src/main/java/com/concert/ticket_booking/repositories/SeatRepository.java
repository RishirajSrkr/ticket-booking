package com.concert.ticket_booking.repositories;

import com.concert.ticket_booking.entity.SeatStatus;
import com.concert.ticket_booking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    //find all seats of an event
    List<Seat>findByEventId(Long eventId);

    //finding only the available seats
    List<Seat> findByEventIdAndBookingStatus(Long eventId, SeatStatus bookingStatus);

    //find all seats that expired
    List<Seat> findByStatusAndLockExpiresAtBefore(SeatStatus bookingStatus, LocalDateTime dateTime);

}
