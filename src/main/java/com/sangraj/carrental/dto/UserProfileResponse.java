package com.sangraj.carrental.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private String name;
    private String email;

    private String phone;
    private String address;
    private String imageUrl;

    private String aadhaarUrl;
    private String drivingLicenseUrl;
    private boolean imageUploaded;
    private boolean aadhaarUploaded;
    private boolean drivingLicenseUploaded;
}

