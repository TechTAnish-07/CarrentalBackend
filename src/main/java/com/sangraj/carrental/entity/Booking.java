package com.sangraj.carrental.entity;

import com.sangraj.carrental.dto.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String location;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private LocalDateTime actualReturnTime;
    private BigDecimal totalAmount;
    // BOOKED, CANCELLED, COMPLETED
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;


}
