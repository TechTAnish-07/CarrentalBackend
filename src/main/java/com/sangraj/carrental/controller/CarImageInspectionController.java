package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.CarInspectionResponse;
import com.sangraj.carrental.dto.InspectionImageDto;
import com.sangraj.carrental.dto.InspectionSide;
import com.sangraj.carrental.dto.InspectionType;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Booking;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.entity.CarInspectionImage;
import com.sangraj.carrental.repository.BookingRepository;
import com.sangraj.carrental.repository.CarInspectionImageRepository;
import com.sangraj.carrental.repository.CarRepository;
import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/car-inspection")
@RequiredArgsConstructor
public class CarImageInspectionController {
   private final CarRepository carRepository;
   private final BookingRepository bookingRepository;
   private final CarInspectionImageRepository carInspectionImageRepo;
   private final ImageUploadService imageUploadService;
   private final UserRepository userRepository;
    @PostMapping("/upload")
    public ResponseEntity<?> uploadInspectionImage(
            @RequestParam Long carId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam InspectionType type,
            @RequestParam InspectionSide side,
            @RequestParam MultipartFile image ,
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new RuntimeException("Unauthenticated request");
        }

        String email = authentication.getName();


        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        Booking booking = bookingId == null ? null :
                bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Replace old image for same side (idempotent)
        AppUser uploader = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        carInspectionImageRepo.findByCarIdAndTypeAndSide(carId, type, side)
                .ifPresent(carInspectionImageRepo::delete);


        String url = imageUploadService.upload(image);

        CarInspectionImage img = new CarInspectionImage();
        img.setCar(car);
        img.setUploader(uploader);
        img.setBooking(booking);
        img.setType(type);
        img.setSide(side);
        img.setImageUrl(url);
        img.setUploadedAt(LocalDateTime.now());

        carInspectionImageRepo.save(img);

        return ResponseEntity.ok("Uploaded " + side + " image (" + type + ")");
    }
    @GetMapping("/booking/{bookingId}/{type}")
    public CarInspectionResponse  getInspection(
            @PathVariable Long bookingId,
            @PathVariable InspectionType type
    ) {
        List<CarInspectionImage> images =
                carInspectionImageRepo.findByBookingIdAndType(bookingId, type);

        CarInspectionResponse response = new CarInspectionResponse();
        response.setBookingId(bookingId);
        response.setType(type);

        for (CarInspectionImage img : images) {
            InspectionImageDto dto =
                    new InspectionImageDto(img.getSide(), img.getImageUrl());

            switch (img.getSide()) {
                case FRONT -> response.setFront(dto);
                case BACK -> response.setBack(dto);
                case LEFT -> response.setLeft(dto);
                case RIGHT -> response.setRight(dto);
            }
        }

        return response;
    }

    @DeleteMapping("/{carId}/{type}/{side}")
    public ResponseEntity<?> deleteInspectionImage(
            @PathVariable Long carId,
            @PathVariable InspectionType type,
            @PathVariable InspectionSide side,
            Authentication authentication
    ) {

        String email = authentication.getName();
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        CarInspectionImage image = carInspectionImageRepo
                .findByCarIdAndTypeAndSide(carId, type, side)
                .orElseThrow(() -> new RuntimeException("Inspection image not found"));

        boolean isAdmin = user.getRole().equals("ROLE_ADMIN");
        boolean isUploader = image.getUploader().getId().equals(user.getId());

        if (!isAdmin && !isUploader) {
            return ResponseEntity.status(403)
                    .body("You are not allowed to delete this image");
        }

        // 4. Delete from cloud (optional but recommended)
       // imageUploadService.delete(image.getImageUrl());

        // 5. Delete from DB
        carInspectionImageRepo.delete(image);

        return ResponseEntity.ok(
                "Deleted " + type + " " + side + " image successfully"
        );
    }

    @GetMapping("/admin/booking/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getInspectionForAdmin(@PathVariable Long bookingId) {

        List<CarInspectionImage> images =
                carInspectionImageRepo.findByBookingId(bookingId);

        return ResponseEntity.ok(
                images.stream().map(img -> Map.of(
                        "type", img.getType(),
                        "side", img.getSide(),
                        "imageUrl", img.getImageUrl(),
                        "uploadedAt", img.getUploadedAt(),
                        "uploadedBy", Map.of(
                                "id", img.getUploader().getId(),
                                "name", img.getUploader().getDisplayName(),
                                "email", img.getUploader().getEmail()
                        )
                )).toList()
        );
    }


}
