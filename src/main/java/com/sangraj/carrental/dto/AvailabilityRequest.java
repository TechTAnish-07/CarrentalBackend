package com.sangraj.carrental.dto;

import lombok.Data;

@Data
public class AvailabilityRequest {
    private String location;
    private String startDateTime;
    private String endDateTime;
}
