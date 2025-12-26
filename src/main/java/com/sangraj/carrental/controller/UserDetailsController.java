package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.ProfileDetailRequest;
import com.sangraj.carrental.dto.UserProfileResponse;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.UserProfile;
import com.sangraj.carrental.repository.UserProfileRepository;
import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/user-detail")
@RequiredArgsConstructor
public class UserDetailsController {

    private final UserRepository repo;
    private final UserProfileRepository userProfileRepository;
    private final ImageUploadService imageUploadService;


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(Authentication authentication) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        repo.delete(user);

        return ResponseEntity.ok("Account deleted successfully");
    }


    @PutMapping("/details")
    public ResponseEntity<?> profileDetail(
            @RequestBody ProfileDetailRequest req,
            Authentication authentication
    ) {
        if (req.getPhone() == null || req.getPhone().isBlank()) {
            throw new RuntimeException("Phone number required");
        }

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        profile.setPhone(req.getPhone());
        profile.setAddress(req.getAddress());

        userProfileRepository.save(profile);

        return ResponseEntity.ok("Profile updated successfully");
    }

    @PostMapping("/kyc")
    public ResponseEntity<?> uploadKyc(
            @RequestParam("aadhaar") MultipartFile aadhaar,
            @RequestParam("drivingLicense") MultipartFile drivingLicense,
            Authentication authentication
    ) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findByUser(user)
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });


        String aadhaarUrl = imageUploadService.upload(aadhaar);
        String dlUrl = imageUploadService.upload(drivingLicense);

        profile.setAadhaarPath(aadhaarUrl);
        profile.setDrivingLicensePath(dlUrl);

        userProfileRepository.save(profile);

        return ResponseEntity.ok("KYC uploaded successfully");
    }

    @DeleteMapping("/kyc/aadhaar")
    public ResponseEntity<?> removeAadhaar(Authentication authentication) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);

        if (profile == null || profile.getAadhaarPath() == null) {
            return ResponseEntity.ok("Aadhaar already removed");
        }

        profile.setAadhaarPath(null);
        userProfileRepository.save(profile);

        return ResponseEntity.ok("Aadhaar removed successfully");
    }
    @DeleteMapping("/kyc/driving-license")
    public ResponseEntity<?> removeDrivingLicense(Authentication authentication) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);

        if (profile == null || profile.getDrivingLicensePath() == null) {
            return ResponseEntity.ok("Driving License already removed");
        }

        profile.setDrivingLicensePath(null);
        userProfileRepository.save(profile);

        return ResponseEntity.ok("Driving License removed successfully");
    }


    // ✅ Get logged-in user profile + KYC
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(user).orElse(null);

        UserProfileResponse response = new UserProfileResponse();
        response.setName(user.getDisplayName());
        response.setEmail(user.getEmail());

        if (profile != null) {
            response.setPhone(profile.getPhone());
            response.setAddress(profile.getAddress());

            response.setAadhaarUrl(profile.getAadhaarPath());
            response.setDrivingLicenseUrl(profile.getDrivingLicensePath());

            response.setAadhaarUploaded(profile.getAadhaarPath() != null);
            response.setDrivingLicenseUploaded(profile.getDrivingLicensePath() != null);
        }

        return ResponseEntity.ok(response);
    }
}
