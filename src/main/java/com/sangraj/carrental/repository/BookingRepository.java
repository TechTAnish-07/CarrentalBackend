package com.sangraj.carrental.repository;
import com.sangraj.carrental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // ================= OVERLAP CHECK =================
    @Query("""
       SELECT b FROM Booking b
       JOIN FETCH b.user
       JOIN FETCH b.car
       WHERE b.status = 'BOOKED'
       """)
    List<Booking> findActiveBookings();

    @Query("""
      SELECT b FROM Booking b
      JOIN FETCH b.user
      JOIN FETCH b.car
      WHERE  b.user.email = :email
       AND (b.status = 'COMPLETED' OR b.status = 'CANCELLED')
""")
    List<Booking> findReturnedBookingsForUser(@Param("email") String email);


    @Query("""
            SELECT b FROM Booking b
            JOIN FETCH b.user
            JOIN FETCH b.car
            WHERE b.status = 'COMPLETED'
             OR b.status = 'CANCELLED'
           """)
    List<Booking> findReturnedBookings();



    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.car.id = :carId
          AND b.startDateTime < :end
          AND b.endDateTime > :start
           AND b.status = 'BOOKED'
    """)
    int countOverlappingBookings(
            Long carId,
            LocalDateTime start,
            LocalDateTime end
    );

    // ================= CURRENT BOOKING CHECK =================

    @Query("""
        SELECT COUNT(b)
        FROM Booking b
        WHERE b.car.id = :carId
          AND :now BETWEEN b.startDateTime AND b.endDateTime
    """)
    int isCarBookedNow(
            Long carId,
            LocalDateTime now
    );

    // ================= ADMIN OPTIMIZED =================

    @Query("""
        SELECT b FROM Booking b
        JOIN FETCH b.user
        JOIN FETCH b.car
    """)
    List<Booking> findAllWithUserAndCar();

    @Query("""
     SELECT COALESCE(SUM(b.totalAmount), 0)
      FROM Booking b
      WHERE b.status = 'COMPLETED'
     """)
    Double getTotalRevenue();


    @Query("""
    SELECT b
    FROM Booking b
    JOIN FETCH b.car
    JOIN FETCH b.user
    WHERE b.user.email = :email
    AND b.status = 'BOOKED'
""")
    List<Booking> findActiveBookingsByUserEmail(
            @Param("email") String email,
            @Param("now") LocalDateTime now
    );
    @Query("""
      SELECT b FROM Booking b
      JOIN FETCH b.car
      WHERE b.id = :bookingId
     """)
    Optional<Booking> findByIdWithCar(Long bookingId);

    @Query("""
    SELECT b
    FROM Booking b
    JOIN FETCH b.user
    JOIN FETCH b.car
    WHERE b.status = 'BOOKED'
""")
    List<Booking> findAllActiveBookings();


    @Query("""
    SELECT COUNT(b)
    FROM Booking b
    WHERE b.car.id = :carId
      AND b.startDateTime < :endDateTime
      AND b.endDateTime > :startDateTime
      AND b.status = 'BOOKED'
""")
    int countOverlappingActiveBookings(
            @Param("carId") Long carId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

}
