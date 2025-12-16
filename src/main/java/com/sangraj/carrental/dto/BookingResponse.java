package com.sangraj.carrental.dto;

import java.time.LocalDate;

public record BookingResponse(
        Long bookingId,
        String userEmail,
        String userName,
        String carModel,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
