package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.AvailabilityRequest;
import com.sangraj.carrental.dto.CarStatusResponse;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.service.BookingService;
import com.sangraj.carrental.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {
    @Autowired
    private CarService carService;



    @PostMapping(value = "/add", consumes = "multipart/form-data")
    public ResponseEntity<Car> addCar(@RequestParam String brand,
                                      @RequestParam String model ,
                                      @RequestParam Integer modelYear,
                                      @RequestParam Integer seats ,
                                      @RequestParam Double pricePerDay ,
                                      @RequestParam String fuelType ,
                                      @RequestParam String location ,
                                      @RequestParam MultipartFile image){
        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setModelYear(modelYear);
        car.setSeats(seats);
        car.setFuelType(fuelType);
        car.setPricePerDay(pricePerDay);
        car.setLocation(location);
        Car savedCar = carService.addCar(car, image);

        return ResponseEntity.ok(savedCar);

    }

    @GetMapping
    public List<Car> getAllCars() {
        return carService.getAllCars();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeCar(@PathVariable Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("Invalid car ID");
        }

        String result = carService.removeCar(id);

        if (result.equals("NOT_FOUND")) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCar(@PathVariable Long id) {
        Car car = carService.getCarById(id);
        return car != null ? ResponseEntity.ok(car) : ResponseEntity.notFound().build();
    }
    @GetMapping("/display/available")
   // @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<List<Car>> getAvailableCars(
            @RequestParam String location,
            @RequestParam String startDateTime,
            @RequestParam String endDateTime
    ) {
        LocalDateTime start = LocalDateTime.parse(startDateTime);
        LocalDateTime end = LocalDateTime.parse(endDateTime);

        List<Car> cars = carService.findAvailableCars(location, start, end);
        return ResponseEntity.ok(cars);
    }


    @GetMapping("/display")
    public ResponseEntity<List<CarStatusResponse>> getCarsForDisplay() {
        return ResponseEntity.ok(carService.getAllCarsForDisplay());
    }

}
