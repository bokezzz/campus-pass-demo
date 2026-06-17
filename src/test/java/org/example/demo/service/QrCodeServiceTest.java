package org.example.demo.service;

import org.example.demo.model.Reservation;
import org.example.demo.model.ReservationStatus;
import org.example.demo.model.ReservationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;

class QrCodeServiceTest {
    @Test
    void qrPayloadContainsMaskedVisitorInformationAndPassCode() {
        Reservation reservation = new Reservation();
        reservation.setType(ReservationType.PUBLIC);
        reservation.setStatus(ReservationStatus.APPROVED);
        reservation.setVisitorName("王小明");
        reservation.setIdentityNo("330102199901010011");
        reservation.setPhone("13812345678");
        reservation.setCampus("朝晖校区");
        reservation.setVisitTime(LocalDateTime.of(2026, 6, 17, 13, 41));
        reservation.setPassCode("CP20260617234204380670");

        String payload = QrCodeService.buildPayload(reservation);

        assertTrue(payload.contains("姓名:王**"));
        assertTrue(payload.contains("身份证号:330************011"));
        assertTrue(payload.contains("通行码:CP20260617234204380670"));
        assertTrue(payload.contains("生成时间:"));
    }
}
