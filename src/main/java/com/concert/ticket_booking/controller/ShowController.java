package com.concert.ticket_booking.controller;

import com.concert.ticket_booking.dto.ShowRequest;
import com.concert.ticket_booking.dto.ShowResponse;
import com.concert.ticket_booking.service.ShowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
