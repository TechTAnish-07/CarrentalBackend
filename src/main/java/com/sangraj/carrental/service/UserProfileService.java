package com.sangraj.carrental.service;

import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.UserProfile;
import com.sangraj.carrental.repository.UserProfileRepository;
import com.sangraj.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public void uploadKyc(
            String email,
            MultipartFile profileImage,
            MultipartFile aadhaar,
            MultipartFile drivingLicense
    ) {

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AppUser managedUser = userRepository.getReferenceById(user.getId());
        UserProfile profile = userProfileRepository
                .findById(managedUser.getId())
                .orElseGet(() -> {
                    UserProfile p = new UserProfile();
                    p.setUser(managedUser);
                    return p;
                });

        if (profileImage != null) {
            profile.setProfileImagePath(
                    imageUploadService.upload(profileImage)
            );
        }

        if (aadhaar != null) {
            profile.setAadhaarPath(
                    imageUploadService.upload(aadhaar)
            );
        }

        if (drivingLicense != null) {
            profile.setDrivingLicensePath(
                    imageUploadService.upload(drivingLicense)
            );
        }
        userProfileRepository.save(profile);
    }
}
