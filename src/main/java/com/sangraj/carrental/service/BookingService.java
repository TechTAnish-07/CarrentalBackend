package com.sangraj.carrental.service;

import com.sangraj.carrental.dto.BookingRequest;
import com.sangraj.carrental.dto.CarStatusResponse;
import com.sangraj.carrental.entity.Booking;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.repository.BookingRepository;
import com.sangraj.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final CarRepository carRepo;

    public BookingService(BookingRepository bookingRepo, CarRepository carRepo) {
        this.bookingRepo = bookingRepo;
        this.carRepo = carRepo;
    }

    public Booking createBooking(BookingRequest request) {

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime());
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime());


        Car car = carRepo.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        int bookedCount = bookingRepo.countOverlappingBookings(
                request.getCarId(), start, end
        );

        if (bookedCount >= car.getQuantity()) {
            throw new RuntimeException("All available cars for this model are already booked.");
        }

        // 4. Create new booking
        Booking booking = new Booking();
        booking.setCarId(car.getId());
        booking.setUserId(request.getUserId());
        booking.setStartDateTime(start);
        booking.setEndDateTime(end);
        booking.setStatus("BOOKED");
        return bookingRepo.save(booking);
    }
    public Booking returnCar(long bookingId, LocalDateTime actualReturnTime) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStatus().equals("BOOKED")) {
            throw new RuntimeException("Car already returned or invalid booking");
        }

        Car car = carRepo.findById(booking.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        LocalDateTime start = booking.getStartDateTime();
        LocalDateTime plannedEnd = booking.getEndDateTime();
        double dailyRate = car.getPricePerDay();
        double hourlyRate = dailyRate / 24;

        // Calculate total hours between start and actual return
        long totalHours = java.time.Duration.between(start, actualReturnTime).toHours();

        double totalAmount;

        if (totalHours <= 24) {
            totalAmount = dailyRate;
        } else {
            long extraHours = totalHours - 24;
            totalAmount = dailyRate + (extraHours * hourlyRate);
        }

        booking.setActualReturnTime(actualReturnTime);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("RETURNED");


        car.setQuantity(car.getQuantity() + 1);
        carRepo.save(car);

        return bookingRepo.save(booking);
    }

}
