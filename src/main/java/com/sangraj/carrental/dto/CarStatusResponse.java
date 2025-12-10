package com.sangraj.carrental.dto;

import lombok.Data;

@Data
public class CarStatusResponse {
    private Long carId;
    private String brand;
    private String model;
    private int modelYear;
    private String fuelType;
    private int seats;
    private double pricePerDay;
    private String imageUrl;
    private String status;
}
