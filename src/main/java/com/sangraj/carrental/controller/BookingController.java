package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.BookingRequest;
import com.sangraj.carrental.dto.BookingResponse;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.service.BookingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ================= USER =================

    @PostMapping("/user/booking")
    @Transactional
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication
    ) {
        try {
            AppUser user = (AppUser) authentication.getPrincipal();
            return ResponseEntity.ok(
                    bookingService.createBooking(bookingRequest, user)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================= ADMIN =================

    @GetMapping("/admin/bookings")
    public List<BookingResponse> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // ================= RETURN =================

    @PostMapping("/booking/return")
    public ResponseEntity<?> returnCar(
            @RequestBody Map<String, String> request
    ) {
        try {
            Long bookingId = Long.parseLong(request.get("bookingId"));
            LocalDateTime actualReturnTime =
                    LocalDateTime.parse(request.get("actualReturnTime"));

            return ResponseEntity.ok(
                    bookingService.returnCar(bookingId, actualReturnTime)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
