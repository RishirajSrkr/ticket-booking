package com.concert.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shows")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column(nullable = false)
    private String theater;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private Double price;

    @OneToMany(mappedBy = "show", fetch = FetchType.LAZY)
    private List<Seat> seats;

}
