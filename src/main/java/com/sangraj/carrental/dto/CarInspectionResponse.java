package com.sangraj.carrental.dto;

import lombok.Data;

@Data
public class CarInspectionResponse {

    private Long BookingId;
    private InspectionType type;

    private InspectionImageDto front;
    private InspectionImageDto back;
    private InspectionImageDto left;
    private InspectionImageDto right;
}
