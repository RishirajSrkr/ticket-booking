package com.concert.ticket_booking.dto;

import com.concert.ticket_booking.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingResponse {

    private Long bookingId;
    private Long showId;
    private String movieTitle;
    private String seatNumber;
    private PaymentStatus paymentStatus;
    private LocalDateTime bookingTime;
    private LocalDateTime lockExpiresAt;
    private String message;
}
