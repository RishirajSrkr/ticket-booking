package com.concert.ticket_booking.dto;

import com.concert.ticket_booking.entity.Movie;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class ShowRequest {

    @NotNull(message = "Movie ID is required")
    private Long movieId;  // ← Just the ID

    @NotNull(message = "Please enter the show date and time.")
    private LocalDateTime datetime;

    @NotBlank(message = "Theater name is required")
    private String theater;

    @Min(value = 1, message = "Total seats must be at least 1.")
    private int totalSeats;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
}