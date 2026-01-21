package com.concert.ticket_booking.service;

import com.concert.ticket_booking.dto.SeatResponse;
import com.concert.ticket_booking.dto.ShowResponse;
import com.concert.ticket_booking.dto.ShowRequest;
import com.concert.ticket_booking.entity.Movie;
import com.concert.ticket_booking.entity.Show;
import com.concert.ticket_booking.entity.Seat;
import com.concert.ticket_booking.entity.SeatStatus;
import com.concert.ticket_booking.repository.MovieRepository;
import com.concert.ticket_booking.repository.ShowRepository;
import com.concert.ticket_booking.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;

    public ShowResponse createShow(ShowRequest showRequest) {

        Movie movie = movieRepository.findById(showRequest.getMovieId()).orElseThrow(() -> new RuntimeException("Movie not found."));

        Show show = new Show();

        show.setMovie(movie);
        show.setTheater(showRequest.getTheater());
        show.setTotalSeats(showRequest.getTotalSeats());
        show.setPrice(showRequest.getPrice());
        show.setDateTime(showRequest.getDatetime());

        show = showRepository.save(show);

        //generating the seats
        List<Seat> seats = generateSeats(show, showRequest.getTotalSeats());

        seatRepository.saveAll(seats);

        return toShowResponse(show);
    }

    private List<Seat> generateSeats(Show show, int totalSeats) {
        List<Seat> seats = new ArrayList<>();

        int seatsPerRow = 10;
        for (int i = 0; i < totalSeats; i++) {
            int rowNumber = (i / seatsPerRow) + 1;
            int seatNumber = (i % seatsPerRow) + 1;

            Seat seat = new Seat();
            seat.setShow(show);
            seat.setSeatNumber("R" + rowNumber + "S" + seatNumber);
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            seat.setLockExpiresAt(null);

            seats.add(seat);

        }
        return seats;
    }

    public List<SeatResponse> getAvailableSeats(Long showId) {
        if (!showRepository.existsById(showId)) {
            throw new RuntimeException("Show not found");
        }
        List<Seat> availableSeats = seatRepository.findByShowIdAndSeatStatus(showId, SeatStatus.AVAILABLE);
        return availableSeats.stream().map(this::toSeatResponse).toList();
    }

    public List<SeatResponse> getAllSeats(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new RuntimeException("Show not found"));

        List<Seat> seats = seatRepository.findByShowId(showId);

        return seats.stream()
                .map(seat -> SeatResponse.builder()
                        .id(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .status(seat.getSeatStatus())
                        .build())
                .toList();
    }

    public List<ShowResponse> getAllShows(){
        List<Show> allShows = showRepository.findAll();
        return allShows.stream().map(this::toShowResponse).toList();
    }

    private SeatResponse toSeatResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getSeatStatus())
                .build();
    }

    private ShowResponse toShowResponse(Show show) {
        return ShowResponse.builder()
                .id(show.getId())
                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())
                .theater(show.getTheater())
                .datetime(show.getDateTime())
                .price(show.getPrice())
                .totalSeats(show.getTotalSeats())
                .build();
    }

}
