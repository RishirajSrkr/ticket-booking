package com.concert.ticket_booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "movies")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String genre;

    private int durationMinutes;

    private String language;

    @OneToMany(mappedBy = "movie", fetch = FetchType.LAZY)
    private List<Show> shows;
}
