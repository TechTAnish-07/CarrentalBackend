package com.sangraj.carrental.service;

import com.sangraj.carrental.dto.BookingRequest;
import com.sangraj.carrental.dto.BookingResponse;
import com.sangraj.carrental.dto.BookingStatus;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Booking;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.repository.BookingRepository;
import com.sangraj.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepo;
    private final CarRepository carRepo;

    public BookingService(BookingRepository bookingRepo, CarRepository carRepo) {
        this.bookingRepo = bookingRepo;
        this.carRepo = carRepo;
    }

    public Booking createBooking(BookingRequest request, AppUser user) {

        LocalDateTime start = LocalDateTime.parse(request.getStartDateTime());
        LocalDateTime end = LocalDateTime.parse(request.getEndDateTime());

        Car car = carRepo.findById(request.getCarId())
                .orElseThrow(() -> new RuntimeException("Car not found"));

        int bookedCount = bookingRepo.countOverlappingBookings(
                car.getId(), start, end
        );

        if (bookedCount >= car.getQuantity()) {
            throw new RuntimeException("All available cars are already booked");
        }

        Booking booking = new Booking();
        booking.setUser(user);          // ✅ OBJECT, not ID
        booking.setCar(car);            // ✅ OBJECT, not ID
        booking.setStartDateTime(start);
        booking.setEndDateTime(end);
        booking.setStatus(BookingStatus.BOOKED);

        return bookingRepo.save(booking);
    }
    public Booking returnCar(Long bookingId, LocalDateTime actualReturnTime) {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.BOOKED) {
            throw new RuntimeException("Car already returned or invalid booking");
        }

        Car car = booking.getCar(); // ✅ No repo call needed

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

        return bookingRepo.save(booking);
    }

    public List<BookingResponse> getAllBookings() {

        return bookingRepo.findAllWithUserAndCar()
                .stream()
                .map(b -> new BookingResponse(
                        b.getId(),
                        b.getUser().getEmail(),
                        b.getUser().getDisplayName(),
                        b.getCar().getModel(),
                        b.getStartDateTime().toLocalDate(),
                        b.getEndDateTime().toLocalDate(),
                        b.getStatus().name()
                ))
                .toList();
    }

}
