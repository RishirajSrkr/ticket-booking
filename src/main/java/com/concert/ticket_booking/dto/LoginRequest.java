package com.concert.ticket_booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Email(message = "Please enter a valid email address")
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
