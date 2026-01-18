package com.concert.ticket_booking.service;

import com.concert.ticket_booking.dto.BookSeatRequest;
import com.concert.ticket_booking.dto.BookingResponse;
import com.concert.ticket_booking.entity.*;
import com.concert.ticket_booking.exception.SeatLockedException;
import com.concert.ticket_booking.repository.BookingRepository;
import com.concert.ticket_booking.repository.SeatRepository;
import com.concert.ticket_booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public BookingResponse createBooking(BookSeatRequest bookSeatRequest){

        //Getting the user from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        //Check if the seat the user is trying to book is already booked
        Seat seat = seatRepository.findById(bookSeatRequest.getSeatId())
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        if(seat.getSeatStatus() != SeatStatus.AVAILABLE){
            throw new RuntimeException("Seat is already " + seat.getSeatStatus().name());
        }

        //Redis locking
        String lockKey = "seat:"+seat.getId()+":lock";
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(
                lockKey,
                user.getName(),
                10,
                TimeUnit.MINUTES
        );
        if(!lockAcquired){
            String lockedByName = redisTemplate.opsForValue().get(lockKey);
            throw new SeatLockedException(
                    "Oops! " + lockedByName + " just grabbed this seat. Try another one!"
            );
        }

        // Update seat status in DB
        seat.setSeatStatus(SeatStatus.LOCKED);
        seat.setLockExpiresAt(LocalDateTime.now().plusMinutes(10));
        seatRepository.save(seat);

        //Create Booking
        Booking booking = new Booking();

        booking.setUser(user);
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setSeat(seat);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setShow(seat.getShow());

        bookingRepository.save(booking);

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(seat.getShow().getId())
                .movieTitle(seat.getShow().getMovie().getTitle())
                .seatNumber(seat.getSeatNumber())
                .paymentStatus(PaymentStatus.PENDING)
                .bookingTime(booking.getBookingDateTime())
                .lockExpiresAt(seat.getLockExpiresAt())
                .message("Seat locked for 10 minutes. Complete payment to confirm.")
                .build();
    }


    public BookingResponse findById(Long id){
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));
        return toBookingResponse(booking);
    }

    public List<BookingResponse> findBookingByShowId(Long showId){
        List<Booking> bookings = bookingRepository.findByShowId(showId);
        return bookings.stream().map(this::toBookingResponse).toList();
    }

    private BookingResponse toBookingResponse(Booking booking){
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .showId(booking.getShow().getId())
                .movieTitle(booking.getShow().getMovie().getTitle())
                .seatNumber(booking.getSeat().getSeatNumber())
                .paymentStatus(booking.getPaymentStatus())
                .bookingTime(booking.getBookingDateTime())
                .build();
    }

}
