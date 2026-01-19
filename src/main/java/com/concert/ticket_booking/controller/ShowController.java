package com.concert.ticket_booking.controller;

import com.concert.ticket_booking.dto.SeatResponse;
import com.concert.ticket_booking.dto.ShowRequest;
import com.concert.ticket_booking.dto.ShowResponse;
import com.concert.ticket_booking.entity.SeatStatus;
import com.concert.ticket_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
@RequiredArgsConstructor
public class ShowController {

    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createEvent(
            @RequestBody ShowRequest showRequest
            ){
        ShowResponse event = showService.createShow(showRequest);
        return ResponseEntity.ok().body(event);
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<SeatResponse>> getSeats(
            @PathVariable Long showId,
            @RequestParam(required = false) SeatStatus status
    ) {
        List<SeatResponse> seats;

        if (status != null) {
            // Get seats by status (e.g., ?status=AVAILABLE)
            seats = showService.getAvailableSeats(showId);
        } else {
            // Get all seats if no status specified
            seats = showService.getAllSeats(showId);
        }

        return ResponseEntity.ok(seats);
    }

}
