package com.sangraj.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InspectionImageDto {
    private InspectionSide side;
    private String imageUrl;
}
