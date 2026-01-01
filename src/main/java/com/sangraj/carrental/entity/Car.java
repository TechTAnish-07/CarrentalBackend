package com.sangraj.carrental.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;

    private int modelYear;
    private String fuelType;
    private int seats;
    @Column(name = "\"pricePerDay\"")
    private BigDecimal pricePerDay;
    private String location;
    private Integer quantity;
    private String imageUrl;
}
