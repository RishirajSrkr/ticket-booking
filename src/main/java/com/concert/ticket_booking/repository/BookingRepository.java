package com.concert.ticket_booking.repository;

import com.concert.ticket_booking.entity.Booking;
import com.concert.ticket_booking.entity.PaymentStatus;
import com.concert.ticket_booking.entity.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    List<Booking> findBySeatId(Long seatId);

    List<Booking> findByShowId(Long showId);

    List<Booking> findBySeatIdPaymentStatus(Long seatId, PaymentStatus paymentStatus);
}
