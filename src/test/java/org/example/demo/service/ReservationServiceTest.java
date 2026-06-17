package org.example.demo.service;

import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservationServiceTest {
    @Test
    void publicReservationIsApprovedAutomatically() {
        Reservation reservation = sample(ReservationType.PUBLIC);

        ReservationService.prepareNewReservation(reservation);

        assertEquals(ReservationStatus.APPROVED, reservation.getStatus());
        assertNotNull(reservation.getPassCode());
    }

    @Test
    void officialReservationWaitsForDepartmentReview() {
        Reservation reservation = sample(ReservationType.OFFICIAL);

        ReservationService.prepareNewReservation(reservation);

        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
        assertNotNull(reservation.getPassCode());
    }

    @Test
    void passCodeIsValidOnTheApprovedVisitDate() {
        Reservation reservation = sample(ReservationType.PUBLIC);
        reservation.setVisitTime(LocalDateTime.of(2026, 6, 16, 10, 0));
        reservation.setStatus(ReservationStatus.APPROVED);

        assertTrue(ReservationService.isPassCodeValid(reservation, LocalDateTime.of(2026, 6, 16, 23, 42)));
        assertFalse(ReservationService.isPassCodeValid(reservation, LocalDateTime.of(2026, 6, 15, 23, 42)));
        assertFalse(ReservationService.isPassCodeValid(reservation, LocalDateTime.of(2026, 6, 17, 0, 1)));
    }

    private Reservation sample(ReservationType type) {
        Reservation reservation = new Reservation();
        reservation.setType(type);
        reservation.setCampus("朝晖校区");
        reservation.setVisitTime(LocalDateTime.of(2026, 6, 16, 10, 0));
        reservation.setOrganization("浙江工业大学校友会");
        reservation.setVisitorName("张三");
        reservation.setIdentityNo("330102199901010011");
        reservation.setPhone("13812345678");
        reservation.setTrafficType("地铁");
        if (type == ReservationType.OFFICIAL) {
            reservation.setVisitDepartmentId(1L);
            reservation.setHostName("李老师");
            reservation.setReason("课程设计答辩交流");
        }
        return reservation;
    }
}
