package com.sangraj.carrental.dto;
import java.time.LocalDateTime;

public record ReturnedBookingResponse(
        Long bookingId,
        Long carId,
        String carName,
        String carImage,
        String fuelType,
        double pricePerDay,
        String userEmail,
        String userName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Double totalAmount,
        BookingStatus bookingStatus
) {}
