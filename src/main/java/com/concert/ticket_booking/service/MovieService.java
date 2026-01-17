package com.concert.ticket_booking.service;

import com.concert.ticket_booking.dto.MovieRequest;
import com.concert.ticket_booking.dto.MovieResponse;
import com.concert.ticket_booking.entity.Movie;
import com.concert.ticket_booking.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = new Movie();
        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setGenre(request.getGenre());
        movie.setDurationMinutes(request.getDurationMinutes());
        movie.setLanguage(request.getLanguage());

        movie = movieRepository.save(movie);

        return toMovieResponse(movie);
    }

    public List<MovieResponse> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toMovieResponse)
                .toList();
    }

    public MovieResponse getMovieByTitle(String title) {
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return toMovieResponse(movie);
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
        return toMovieResponse(movie);
    }

    private MovieResponse toMovieResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .genre(movie.getGenre())
                .durationMinutes(movie.getDurationMinutes())
                .language(movie.getLanguage())
                .build();
    }
}