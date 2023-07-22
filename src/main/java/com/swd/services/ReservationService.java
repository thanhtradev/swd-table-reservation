package com.swd.services;


import com.swd.constraints.EReservationStatus;
import com.swd.entities.Reservation;
import com.swd.exception.EntityNotFoundException;
import com.swd.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Reservation.class, "reservation_id", id.toString()));
    }

    public Reservation getByUserIdAndTableId(Long userId, Long tableId) {
        return reservationRepository.findByUserIdAndTableId(userId, tableId);
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Boolean existsById(Long id) {
        return reservationRepository.existsById(id);
    }

    public void deleteReservationById(Long id) {
        reservationRepository.deleteById(id);
    }

    public Boolean isValidStatus(String status) {
        for (EReservationStatus e : EReservationStatus.values()) {
            if (e.name().equals(status)) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAllAcceptedReservationsInTime(Date startTime, Date endTime) {
        return reservationRepository.findAllApprovedReservationsInTime(startTime, endTime);
    }

    public List<Reservation> getAllByDate(Date date) {
        return reservationRepository.findAllByDate(date);
    }

    public List<Reservation> getAllByDateAndUserId(Date date, Long userId) {
        return reservationRepository.findAllByDateAndUserId(date, userId);
    }

    public void rejectReservation(Reservation reservation) {
        List<Reservation> reservations = reservationRepository.findAllByStartTimeAndStatusPending(reservation.getStartTime());
        for (Reservation res : reservations) {
            res.setStatus(EReservationStatus.REJECTED);
            reservationRepository.save(res);
        }
    }
}
