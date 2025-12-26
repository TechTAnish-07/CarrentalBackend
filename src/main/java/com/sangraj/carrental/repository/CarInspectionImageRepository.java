package com.sangraj.carrental.repository;

import com.sangraj.carrental.dto.InspectionSide;
import com.sangraj.carrental.dto.InspectionType;
import com.sangraj.carrental.entity.CarInspectionImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarInspectionImageRepository
        extends JpaRepository<CarInspectionImage, Long> {

    List<CarInspectionImage> findByCarIdAndType(Long carId, InspectionType type);

    Optional<CarInspectionImage> findByCarIdAndTypeAndSide(
            Long carId,
            InspectionType type,
            InspectionSide side
    );

    List<CarInspectionImage> findByBookingId(Long bookingId);

    List<CarInspectionImage> findByBookingIdAndType(Long BookingId, InspectionType type);
}
