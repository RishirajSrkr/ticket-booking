package com.concert.ticket_booking.dto;

import com.concert.ticket_booking.entity.Movie;
import com.concert.ticket_booking.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShowResponse {

    private Long id;
    private Long movieId;
    private String movieTitle;
    private LocalDateTime datetime;
    private int totalSeats;
    private String theater;
    private Double price;
}
