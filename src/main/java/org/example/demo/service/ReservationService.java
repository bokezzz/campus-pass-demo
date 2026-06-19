package org.example.demo.service;

import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.example.demo.util.SecurityUtil;

import java.time.LocalDateTime;

public final class ReservationService {
    private ReservationService() {
    }

    public static void prepareNewReservation(Reservation reservation) {
        reservation.setApplyTime(LocalDateTime.now());
        reservation.setPassCode(SecurityUtil.generatePassCode());
        if (reservation.getType() == ReservationType.PUBLIC) {
            reservation.setStatus(ReservationStatus.APPROVED);
        } else {
            reservation.setStatus(ReservationStatus.PENDING);
        }
    }

    public static boolean isPassCodeValid(Reservation reservation, LocalDateTime now) {
        if (reservation == null || reservation.getStatus() != ReservationStatus.APPROVED || reservation.getVisitTime() == null) {
            return false;
        }
        return reservation.getVisitTime().toLocalDate().equals(now.toLocalDate());
    }

    public static int peopleCount(Reservation reservation) {
        if (reservation == null || reservation.getCompanions() == null || reservation.getCompanions().isBlank()) {
            return 1;
        }
        String normalized = reservation.getCompanions().replace("\r\n", "\n").trim();
        return 1 + normalized.split("\\n+").length;
    }
}
