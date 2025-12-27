package com.sangraj.carrental.service;
import com.sangraj.carrental.dto.CarStatusResponse;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.repository.BookingRepository;
import com.sangraj.carrental.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CarService {

    private final CarRepository carRepo;
    private final BookingRepository bookingRepo;
    private final ImageUploadService imageUploadService;

    public CarService(CarRepository carRepo, BookingRepository bookingRepo, ImageUploadService imageUploadService) {
        this.carRepo = carRepo;
        this.bookingRepo = bookingRepo;
        this.imageUploadService = imageUploadService;
    }

    public Car addCar(Car car, MultipartFile imageFile) {

        Optional<Car> existingCar = carRepo.findByBrandAndModelAndLocation(
                car.getBrand(),
                car.getModel(),
                car.getLocation()
        );


        if (existingCar.isPresent()) {
            Car oldCar = existingCar.get();
            oldCar.setQuantity(oldCar.getQuantity() + 1);

            if (imageFile != null && !imageFile.isEmpty()) {
                String url = imageUploadService.upload(imageFile);
                oldCar.setImageUrl(url);
            }

            return carRepo.save(oldCar);
        }

        String imageUrl = imageUploadService.upload(imageFile);
        car.setImageUrl(imageUrl);

        car.setQuantity(1);

        return carRepo.save(car);
    }


    public List<Car> getAllCars() {
        return carRepo.findAll();
    }
    public String removeCar(Long id) {
        Car car = carRepo.findById(id).orElse(null);

        if (car == null) {
            return "NOT_FOUND";
        }

        if (car.getQuantity() > 1) {
            car.setQuantity(car.getQuantity() - 1);
            carRepo.save(car);
            return "Car quantity decreased to: " + car.getQuantity();
        } else {
            carRepo.delete(car);
            return "Car deleted because quantity reached 1";
        }
    }

    public Car getCarById(long id) {
        return carRepo.findById(id).orElse(null);
    }

    public List<Car> getCarsByLocation(String location) {
        return carRepo.findByLocation(location);
    }

    public List<Car> findAvailableCars(String location, LocalDateTime start, LocalDateTime end) {
        List<Car> cars = carRepo.findByLocation(location);
        List<Car> availableCars = new ArrayList<>();

        for (Car car : cars) {
            int clashes = bookingRepo.countOverlappingActiveBookings(
                    car.getId(), start, end
            );
           if(clashes < car.getQuantity()){
             availableCars.add(car);
           }
        }

        return availableCars; 
    }

    public List<CarStatusResponse> getAllCarsForDisplay() {

        List<Car> cars = carRepo.findAll();
        LocalDateTime now = LocalDateTime.now();

        List<CarStatusResponse> result = new ArrayList<>();

        for (Car car : cars) {

            int activeBookings = bookingRepo.isCarBookedNow(car.getId(), now);

            CarStatusResponse dto = new CarStatusResponse();
            dto.setCarId(car.getId());
            dto.setBrand(car.getBrand());
            dto.setModel(car.getModel());
            dto.setModelYear(car.getModelYear());
            dto.setFuelType(car.getFuelType());
            dto.setSeats(car.getSeats());
            dto.setPricePerDay(car.getPricePerDay());
            dto.setLocation(car.getLocation());
            dto.setQuantity(car.getQuantity());
            dto.setImageUrl(car.getImageUrl());

            if (activeBookings >= car.getQuantity()) {
                dto.setStatus("Booked");
            } else {
                dto.setStatus("Available");
            }

            result.add(dto);
        }

        return result;
    }

    public void updateQuantity(Long carId, int delta) {
        Car car = carRepo.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        int newQty = car.getQuantity() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        car.setQuantity(newQty);
        carRepo.save(car);
    }
}
