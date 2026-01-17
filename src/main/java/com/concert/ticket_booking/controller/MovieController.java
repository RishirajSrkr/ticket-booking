package com.concert.ticket_booking.controller;

import com.concert.ticket_booking.dto.MovieRequest;
import com.concert.ticket_booking.dto.MovieResponse;
import com.concert.ticket_booking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMovies();
        return ResponseEntity.status(HttpStatus.OK).body(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable Long id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.status(HttpStatus.OK).body(movie);
    }
}
