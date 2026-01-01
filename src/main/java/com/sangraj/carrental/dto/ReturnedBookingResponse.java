package com.sangraj.carrental.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReturnedBookingResponse(
        Long bookingId,
        Long carId,
        String carName,
        String carImage,
        String fuelType,
        BigDecimal pricePerDay,
        String userEmail,
        String userName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal totalAmount,
        BookingStatus bookingStatus
) {}
