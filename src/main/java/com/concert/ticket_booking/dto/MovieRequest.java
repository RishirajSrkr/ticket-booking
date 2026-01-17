package com.concert.ticket_booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MovieRequest {

    @NotBlank(message = "Movie title is required")
    private String title;

    private String description;

    @NotBlank(message = "Genre is required")
    private String genre;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotBlank(message = "Language is required")
    private String language;
}
