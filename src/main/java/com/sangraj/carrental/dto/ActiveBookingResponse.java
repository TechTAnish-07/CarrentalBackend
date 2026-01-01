package com.sangraj.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ActiveBookingResponse(
        Long bookingId,
        // 🔹 Car info
         Long carId,
        String carName,
       String carImage,
         String fuelType,
         BigDecimal pricePerDay,
        LocalDate startDate,
        LocalDate endDate,
        String bookingStatus
) {}
