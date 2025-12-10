package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.carId = :carId AND " +
            "(b.startDateTime < :endDateTime AND b.endDateTime > :startDateTime)")
    List<Booking> findOverlappingBookings(Long carId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.carId = :carId AND " +
            "(b.startDateTime < :end AND b.endDateTime > :start)")
    int countOverlappingBookings(Long carId,
                                 LocalDateTime start,
                                 LocalDateTime end);

    @Query("""
    SELECT COUNT(b)\s
    FROM Booking b\s
    WHERE b.carId = :carId\s
      AND :now BETWEEN b.startDateTime AND b.endDateTime
""")
    int isCarBookedNow(Long carId, LocalDateTime now);

}
