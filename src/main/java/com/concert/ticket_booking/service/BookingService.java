package com.concert.ticket_booking.service;

import com.concert.ticket_booking.dto.BookSeatRequest;
import com.concert.ticket_booking.dto.BookingResponse;
import com.concert.ticket_booking.entity.*;
import com.concert.ticket_booking.exception.SeatLockedException;
import com.concert.ticket_booking.repository.BookingRepository;
import com.concert.ticket_booking.repository.SeatRepository;
import com.concert.ticket_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private static final int LOCK_DURATION_MINUTES = 3;

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public BookingResponse createBooking(BookSeatRequest bookSeatRequest) {
        User user = getCurrentUser();

        log.info("User {} attempting to book seat {}", user.getEmail(), bookSeatRequest.getSeatId());

        Seat seat = seatRepository.findById(bookSeatRequest.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if (seat.getSeatStatus() != SeatStatus.AVAILABLE) {
            log.warn("Seat {} is not available. Current status: {}", seat.getId(), seat.getSeatStatus());
            throw new RuntimeException("Seat is already " + seat.getSeatStatus().name());
        }

        // Redis locking
        String lockKey = "seat:" + seat.getId() + ":lock";
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                user.getName(),
                LOCK_DURATION_MINUTES,
                TimeUnit.MINUTES
        );


        if (!lockAcquired) {
            String lockedByName = redisTemplate.opsForValue().get(lockKey);
            log.warn("Seat {} already locked by {}", seat.getId(), lockedByName);
            throw new SeatLockedException(
                    "Oops! " + lockedByName + " just grabbed this seat. Try another one!"
            );
        }

        seat.setSeatStatus(SeatStatus.LOCKED);
        seat.setLockExpiresAt(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        seatRepository.save(seat);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setSeat(seat);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setShow(seat.getShow());
        bookingRepository.save(booking);

        log.info("Booking {} created successfully for user {} and seat {}",
                booking.getId(), user.getEmail(), seat.getId());

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(seat.getShow().getId())
                .movieTitle(seat.getShow().getMovie().getTitle())
                .seatNumber(seat.getSeatNumber())
                .paymentStatus(PaymentStatus.PENDING)
                .bookingTime(booking.getBookingDateTime())
                .lockExpiresAt(seat.getLockExpiresAt())
                .message("Seat locked for " + LOCK_DURATION_MINUTES + " minutes. Complete payment to confirm.")
                .build();
    }

    @Transactional
    public BookingResponse completePayment(Long bookingId) {
        User currentUser = getCurrentUser();

        log.info("User {} attempting to complete payment for booking {}", currentUser.getEmail(), bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getPaymentStatus() == PaymentStatus.COMPLETED) {
            log.warn("Payment already completed for booking {}", bookingId);
            throw new RuntimeException("Payment already completed");
        }

        // Authorization check
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            log.error("Unauthorized payment attempt by {} for booking {}", currentUser.getEmail(), bookingId);
            throw new RuntimeException("Unauthorized: This is not your booking");
        }

        Seat seat = booking.getSeat();

        // Seat status validation
        if (seat.getSeatStatus() == SeatStatus.BOOKED) {
            log.error("Seat {} already booked for booking {}", seat.getId(), bookingId);
            throw new RuntimeException("This seat is already booked by someone else.");
        }

        if (seat.getSeatStatus() == SeatStatus.AVAILABLE) {
            log.warn("Seat {} was released for expired booking {}", seat.getId(), bookingId);
            throw new RuntimeException("Booking expired. Seat was released. Please book again.");
        }

        if (seat.getSeatStatus() != SeatStatus.LOCKED) {
            log.error("Invalid seat status {} for booking {}", seat.getSeatStatus(), bookingId);
            throw new RuntimeException("Invalid seat status: " + seat.getSeatStatus());
        }

        // Lock expiry check
        if (seat.getLockExpiresAt() == null) {
            log.error("Locked seat {} has no expiry time", seat.getId());
            throw new RuntimeException("Invalid state: Locked seat has no expiry time");
        }

        if (seat.getLockExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Booking {} expired at {}", bookingId, seat.getLockExpiresAt());
            throw new RuntimeException("Booking expired. Please book again.");
        }

        // Complete payment
        seat.setSeatStatus(SeatStatus.BOOKED);
        seat.setLockExpiresAt(null);
        seatRepository.save(seat);

        booking.setPaymentStatus(PaymentStatus.COMPLETED);
        bookingRepository.save(booking);

        String lockKey = "seat:" + seat.getId() + ":lock";
        redisTemplate.delete(lockKey);

        log.info("Payment completed successfully for booking {}", bookingId);

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(seat.getShow().getId())
                .movieTitle(seat.getShow().getMovie().getTitle())
                .seatNumber(seat.getSeatNumber())
                .paymentStatus(PaymentStatus.COMPLETED)  // ✅ Fixed
                .bookingTime(booking.getBookingDateTime())
                .message("Payment successful! Enjoy the movie!")
                .build();
    }

    public BookingResponse findById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        return toBookingResponse(booking);
    }

    public List<BookingResponse> getUserBookings() {
        User user = getCurrentUser();
        List<Booking> userBookings = bookingRepository.findByUserIdOrderByBookingDateTimeDesc(user.getId());
        return userBookings.stream().map(this::toBookingResponse).toList();
    }

    public List<BookingResponse> findBookingByShowId(Long showId) {
        List<Booking> bookings = bookingRepository.findByShowId(showId);
        return bookings.stream().map(this::toBookingResponse).toList();
    }

    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream().map(this::toBookingResponse).toList();
    }

    private BookingResponse toBookingResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(booking.getShow().getId())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .seatNumber(booking.getSeat().getSeatNumber())
                .paymentStatus(booking.getPaymentStatus())
                .bookingTime(booking.getBookingDateTime())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}