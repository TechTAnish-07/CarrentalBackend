package com.sangraj.carrental.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_profiles")
public class UserProfile {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private AppUser user;

    private String phone;
    private String address;

    private String profileImagePath;

    private String aadhaarPath;
    private String drivingLicensePath;



}
