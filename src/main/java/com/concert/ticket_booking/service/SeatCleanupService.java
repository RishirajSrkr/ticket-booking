package com.concert.ticket_booking.service;

import com.concert.ticket_booking.entity.Booking;
import com.concert.ticket_booking.entity.PaymentStatus;
import com.concert.ticket_booking.entity.Seat;
import com.concert.ticket_booking.entity.SeatStatus;
import com.concert.ticket_booking.repository.BookingRepository;
import com.concert.ticket_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatCleanupService {

    private final SeatRepository seatRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final BookingRepository bookingRepository;

    @Scheduled(fixedRate = 30000)
    @Transactional
    public void releaseExpiredLocks() {
        log.info("Running scheduled cleanup job to release expired seat locks...");

        LocalDateTime now = LocalDateTime.now();
        List<Seat> expiredSeats = seatRepository.findBySeatStatusAndLockExpiresAtBefore(SeatStatus.LOCKED, now);

        if (expiredSeats.isEmpty()) {
            log.info("No expired seats found");
            return;
        }
        log.info("Found {} expired seat locks", expiredSeats.size());

        for (Seat seat : expiredSeats) {

            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockExpiresAt(null);
            seatRepository.save(seat);

            List<Booking> pendingBookings = bookingRepository.findBySeatIdAndPaymentStatus(seat.getId(), PaymentStatus.PENDING);
            pendingBookings.forEach(booking -> {
                booking.setPaymentStatus(PaymentStatus.EXPIRED);
                log.info("Marked booking {} as EXPIRED", booking.getId());
            });
            bookingRepository.saveAll(pendingBookings);

            //delete redis lock
            String lockKey = "seat:" + seat.getId() + ":lock";
            Boolean deleted = redisTemplate.delete(lockKey);

            log.info("Released seat {} (Show: {}, Seat Number: {}). Redis lock deleted: {}", seat.getId(), seat.getShow().getId(), seat.getSeatNumber(), deleted);
        }

        log.info("Cleanup complete. Released {} expired locks", expiredSeats.size());


    }
}
