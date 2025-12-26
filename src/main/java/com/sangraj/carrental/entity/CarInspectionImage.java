package com.sangraj.carrental.entity;

import com.sangraj.carrental.dto.InspectionSide;
import com.sangraj.carrental.dto.InspectionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "car_inspection_images")
@Data
public class CarInspectionImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private AppUser uploader;
    @Enumerated(EnumType.STRING)
    private InspectionType type; // BEFORE / AFTER

    @Enumerated(EnumType.STRING)
    private InspectionSide side; // FRONT, BACK, LEFT, RIGHT...

    private String imageUrl;

    private LocalDateTime uploadedAt;
}
