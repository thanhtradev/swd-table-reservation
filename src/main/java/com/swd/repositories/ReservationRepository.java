package com.swd.repositories;


import com.swd.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {
    Reservation findByUserIdAndTableId(Long userId, Long tableId);

    @Query("SELECT r FROM Reservation r WHERE r.startTime >= ?1 AND r.endTime <= ?2 AND r.status = 'APPROVED'")
    List<Reservation> findAllApprovedReservationsInTime(Date startTime, Date endTime);

    // This is a custom query that finds all reservation has startTime and endTime in the same day as the given date
    @Query("SELECT r FROM Reservation r WHERE DATE(r.startTime) = :date")
    List<Reservation> findAllByDate(@Param("date") Date date);

    @Query("SELECT r FROM Reservation r WHERE r.startTime = ?1 AND r.status = 'PENDING'")
    List<Reservation> findAllByStartTimeAndStatusPending(Date startTime);
}
