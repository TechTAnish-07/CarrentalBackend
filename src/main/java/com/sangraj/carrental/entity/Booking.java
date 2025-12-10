package com.sangraj.carrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long carId;
    private long userId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime actualReturnTime;
    private Double totalAmount;
    private String status; // BOOKED, CANCELLED, COMPLETED


}
