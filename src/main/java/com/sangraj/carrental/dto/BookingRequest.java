package com.sangraj.carrental.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long carId;
    private String startDateTime;   // "2025-12-01T10:00"
    private String endDateTime;     // "2025-12-03T18:00"
    private Long userId;




    // optional
}
