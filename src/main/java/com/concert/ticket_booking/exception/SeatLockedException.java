package com.concert.ticket_booking.exception;

public class SeatLockedException extends RuntimeException{

    public SeatLockedException(String message){
        super(message);
    }
}
