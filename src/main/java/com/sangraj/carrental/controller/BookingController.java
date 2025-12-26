package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.ActiveBookingResponse;
import com.sangraj.carrental.dto.AdminActiveBookingResponse;
import com.sangraj.carrental.dto.BookingRequest;
import com.sangraj.carrental.dto.ReturnedBookingResponse;

import com.sangraj.carrental.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;
    @PostMapping("/user/booking")
    public ResponseEntity<?> createBooking(
            @RequestBody BookingRequest bookingRequest,
            Authentication authentication
    ) {
        try {
            String userEmail =  authentication.getName();
            System.out.println(userEmail);
            return ResponseEntity.ok(
                    bookingService.createBooking(bookingRequest, userEmail)
            );
        } catch (Exception e) {
            throw e; // or custom exception
        }
    }

    @PostMapping("/user/booking/return/{bookingId}")
    public ResponseEntity<?> returnCar(@PathVariable Long bookingId) {
        try {
            bookingService.returnCar(bookingId, LocalDateTime.now());
            return ResponseEntity.ok("Car returned successfully");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/user/booking/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            return ResponseEntity.ok(
                    bookingService.cancelBooking(bookingId)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/booking/active")
    public ResponseEntity<List<ActiveBookingResponse>> getUserActiveBookings(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        List<ActiveBookingResponse> bookings =
                bookingService.getActiveBookingsForUser(userEmail);

        return ResponseEntity.ok(bookings);
    }
    @GetMapping("/user/booking/history")
    public ResponseEntity<List<ReturnedBookingResponse>>getUserHistoryBooking(   Authentication authentication){
     String userEmail = authentication.getName();
     List<ReturnedBookingResponse> bookings = bookingService.getHistoryBookingForUser(userEmail);
     return ResponseEntity.ok(bookings);
    }
    // Active bookings (currently running)
    @GetMapping("/admin/bookings/active")
    public ResponseEntity<List<AdminActiveBookingResponse>> getAllActiveBookings() {
        return ResponseEntity.ok(bookingService.getAllActiveBookings());
    }

    // Returned booking history
    @GetMapping("/admin/bookings/history")
    public List<ReturnedBookingResponse> getBookingHistory() {
        return bookingService.getReturnedBookings();
    }

    // Total revenue
    @GetMapping("/admin/bookings/revenue")
    public Double getTotalRevenue() {
        return bookingService.getTotalRevenue();
    }

}
