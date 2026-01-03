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
                .findById(user.getId())
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
            @RequestParam(required = false) MultipartFile aadhaar,
            @RequestParam(required = false) MultipartFile drivingLicense,
            @RequestParam(required = false) MultipartFile profileImage,
            Authentication authentication
    ) {
        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository
                .findById(user.getId())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(user);
                    return p;
                });

        if (profileImage != null) {
            profile.setProfileImagePath(imageUploadService.upload(profileImage));
        }
        if (aadhaar != null) {
            profile.setAadhaarPath(imageUploadService.upload(aadhaar));
        }
        if (drivingLicense != null) {
            profile.setDrivingLicensePath(imageUploadService.upload(drivingLicense));
        }

        userProfileRepository.save(profile);

        return ResponseEntity.ok("KYC updated successfully");
    }


    @DeleteMapping("/kyc/aadhaar")
    public ResponseEntity<?> removeAadhaar(Authentication authentication) {

        AppUser user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfile profile = userProfileRepository.findById(user.getId())
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

        UserProfile profile = userProfileRepository.findById(user.getId())
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

        UserProfile profile = userProfileRepository.findById(user.getId()).orElse(null);

        UserProfileResponse response = new UserProfileResponse();
        response.setName(user.getDisplayName());
        response.setEmail(user.getEmail());

        if (profile != null) {
            response.setPhone(profile.getPhone());
            response.setAddress(profile.getAddress());
            response.setImageUrl(profile.getProfileImagePath());
            response.setAadhaarUrl(profile.getAadhaarPath());
            response.setDrivingLicenseUrl(profile.getDrivingLicensePath());

            response.setAadhaarUploaded(profile.getAadhaarPath() != null);
            response.setDrivingLicenseUploaded(profile.getDrivingLicensePath() != null);
        }

        return ResponseEntity.ok(response);
    }
}
