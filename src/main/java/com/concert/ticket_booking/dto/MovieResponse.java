package com.concert.ticket_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieResponse {

    private Long id;
    private String title;
    private String description;
    private String genre;
    private Integer durationMinutes;
    private String language;
}