package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.BookingRequest;
import com.sangraj.carrental.service.BookingService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping("/booking")
    @Transactional
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        try {
            var booking = bookingService.createBooking(bookingRequest);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping("/booking/return")
    public ResponseEntity<?> returnCar(@RequestBody Map<String, String> request) {
        try {
            long bookingId = Long.parseLong(request.get("bookingId"));
            LocalDateTime actualReturnTime = LocalDateTime.parse(request.get("actualReturnTime"));

            var updatedBooking = bookingService.returnCar(bookingId, actualReturnTime);
            return ResponseEntity.ok(updatedBooking);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
