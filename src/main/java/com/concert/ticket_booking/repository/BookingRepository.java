package com.concert.ticket_booking.repository;

import com.concert.ticket_booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking,Long > {
    List<Booking> findByUserId(Long userId);
    List<Booking> findBySeatId(Long seatId);
    List<Booking> findByShowId(Long showId);
}
