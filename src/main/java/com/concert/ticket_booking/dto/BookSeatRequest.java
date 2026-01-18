package com.concert.ticket_booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookSeatRequest {

    @NotNull(message = "Seat ID is required")
    private Long seatId;

    @NotNull(message = "Show ID is required")
    private Long showId;

}
