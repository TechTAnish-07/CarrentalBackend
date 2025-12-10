package com.sangraj.carrental.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private double pricePerDay;
    private String location;
    private int quantity;
    private String imageUrl;
}
