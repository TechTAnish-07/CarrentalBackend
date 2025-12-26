package com.sangraj.carrental.service;

import com.sangraj.carrental.dto.*;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Booking;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.repository.BookingRepository;
import com.sangraj.carrental.repository.CarRepository;
import com.sangraj.carrental.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {
    @Autowired
    private UserRepository userRepository;
    private final BookingRepository bookingRepo;
    private final CarRepository carRepo;

    public BookingService(BookingRepository bookingRepo, CarRepository carRepo) {
        this.bookingRepo = bookingRepo;
        this.carRepo = carRepo;
    }
     @Transactional
    public Booking createBooking(BookingRequest request,  String email) {


      AppUser user = userRepository.findByEmail(email)
              .orElseThrow(() -> new RuntimeException("User not found"));
      LocalDateTime start = LocalDateTime.parse(request.getStartDateTime());
      LocalDateTime end = LocalDateTime.parse(request.getEndDateTime());
         if (user == null || user.getId() == null) {
             throw new IllegalStateException("User not authenticated");
         }
        Car car = carRepo.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        int bookedCount = bookingRepo.countOverlappingBookings(
                car.getId(), start, end
        );

        if (bookedCount >= car.getQuantity()) {
            throw new RuntimeException("All available cars are already booked");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setCar(car);
        booking.setStartDateTime(start);
        booking.setEndDateTime(end);
        booking.setStatus(BookingStatus.BOOKED);
        booking.setLocation(request.getLocation());

      Booking saved = bookingRepo.save(booking);

      System.out.println("BOOKING SAVED WITH ID: " + saved.getId());

      return saved;
    }
    public void returnCar(Long bookingId, LocalDateTime actualReturnTime) {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));



        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Car already returned or invalid booking");
        }

        if (now.isBefore(booking.getStartDateTime())) {
            throw new IllegalStateException(
                    "You cannot return the car before the booking start time"
            );
        }
        Car car = booking.getCar();

        LocalDateTime start = booking.getStartDateTime();
        double dailyRate = car.getPricePerDay();
        double hourlyRate = dailyRate / 24;

        long totalHours = java.time.Duration
                .between(start, actualReturnTime)
                .toHours();

        double totalAmount;

        if (totalHours <= 24) {
            totalAmount = dailyRate;
        } else {
            totalAmount = dailyRate + (totalHours - 24) * hourlyRate;
        }

        booking.setActualReturnTime(actualReturnTime);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.COMPLETED);

        bookingRepo.save(booking);
    }

    public List<AdminActiveBookingResponse> getAllActiveBookings() {

        LocalDateTime now = LocalDateTime.now();

        return bookingRepo.findAllActiveBookings()
                .stream()
                .map(b -> new AdminActiveBookingResponse(
                        b.getId(),

                        // User
                        b.getUser().getDisplayName(),
                        b.getUser().getEmail(),

                        // Car
                        b.getCar().getId(),
                        b.getCar().getBrand() + " " + b.getCar().getModel(),
                        b.getCar().getImageUrl(),

                        // Booking
                        b.getStartDateTime().toLocalDate(),
                        b.getEndDateTime().toLocalDate(),
                        b.getStatus().name()
                ))
                .toList();
    }


    public List<ReturnedBookingResponse> getReturnedBookings() {

        return bookingRepo.findReturnedBookings()
                .stream()
                .map(b -> new ReturnedBookingResponse(
                        b.getId(),
                        b.getCar().getId(),
                        b.getCar().getModel(),
                        b.getCar().getImageUrl(),
                        b.getCar().getFuelType(),
                        b.getCar().getPricePerDay(),
                        b.getUser().getEmail(),
                        b.getUser().getDisplayName(),
                        b.getStartDateTime(),
                        b.getActualReturnTime(),
                        b.getTotalAmount(),
                        b.getStatus()
                ))
                .toList();
    }
    public Double getTotalRevenue() {
        return bookingRepo.getTotalRevenue();
    }

    public List<ActiveBookingResponse> getActiveBookingsForUser(String userEmail) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> activeBookings =
                bookingRepo.findActiveBookingsByUserEmail(userEmail, now);

        return activeBookings.stream()
                .map(b -> new ActiveBookingResponse(
                        b.getId(),
                        b.getCar().getId(),
                        b.getCar().getBrand() + " " + b.getCar().getModel(),
                        b.getCar().getImageUrl(),
                        b.getCar().getFuelType(),
                        b.getCar().getPricePerDay(),
                        b.getStartDateTime().toLocalDate(),
                        b.getEndDateTime().toLocalDate(),
                        b.getStatus().name()
                ))
                .toList();

    }

    public List<ReturnedBookingResponse> getHistoryBookingForUser(String userEmail) {

        List<Booking> historyBookings =
                bookingRepo.findReturnedBookingsForUser(
                        userEmail);


        if (historyBookings.isEmpty()) {
            return List.of();
        }

        return historyBookings.stream()
                .map(b -> new ReturnedBookingResponse(
                        b.getId(),
                        b.getCar().getId(),
                        b.getCar().getModel(),
                        b.getCar().getImageUrl(),
                        b.getCar().getFuelType(),
                        b.getCar().getPricePerDay(),
                        b.getUser().getEmail(),
                        b.getUser().getDisplayName(),
                        b.getStartDateTime(),
                        b.getEndDateTime() != null
                                ? b.getEndDateTime()
                                : null,
                        b.getTotalAmount(),
                        b.getStatus()
                ))
                .toList();
    }

    @Transactional
    public String cancelBooking(Long bookingId) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Booking not found")
                );

        LocalDateTime now = LocalDateTime.now();


        if (!booking.getStartDateTime().isAfter(now)) {
            throw new IllegalStateException(
                    "Booking cannot be cancelled after start time"
            );
        }


        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new IllegalStateException(
                    "Only active bookings can be cancelled"
            );
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);

        return "Booking cancelled successfully";
    }

}
