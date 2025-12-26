package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByBrandAndModelAndLocation(String brand, String model, String location);

    List<Car> findByLocation(String location);
}
