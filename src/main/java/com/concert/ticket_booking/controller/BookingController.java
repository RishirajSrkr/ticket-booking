package com.concert.ticket_booking.controller;

import com.concert.ticket_booking.dto.BookSeatRequest;
import com.concert.ticket_booking.dto.BookingResponse;
import com.concert.ticket_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookSeatRequest bookSeatRequest
            ){
        BookingResponse booking = bookingService.createBooking(bookSeatRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);

    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> findById(
            @PathVariable(name = "id") Long bookingId
    ){
        BookingResponse booking = bookingService.findById(bookingId);
        return ResponseEntity.status(HttpStatus.OK).body(booking);

    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<List<BookingResponse>> findBookingByShowId(
            @PathVariable Long showId
    ){
        List<BookingResponse> bookings = bookingService.findBookingByShowId(showId);
        return ResponseEntity.status(HttpStatus.OK).body(bookings);

    }

}
