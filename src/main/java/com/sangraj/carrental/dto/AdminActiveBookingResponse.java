package com.sangraj.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminActiveBookingResponse {

    private Long bookingId;

    // 👤 User
    private String userName;
    private String userEmail;

    // 🚗 Car
    private Long carId;
    private String carName;
    private String carImage;

    // 📅 Booking
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
