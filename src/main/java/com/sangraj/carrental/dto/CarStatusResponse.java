package com.sangraj.carrental.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CarStatusResponse {
    private Long carId;
    private String brand;
    private String model;
    private int modelYear;
    private String fuelType;
    private int seats;
    private BigDecimal pricePerDay;
    private String imageUrl;
    private String status;
    private String location;
    private Integer quantity;
}
