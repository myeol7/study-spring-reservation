package com.example.studyspringreservation.reservation;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import com.example.studyspringreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationCancelTest {

    @Autowired
    ReservationService reservationService;

    @Test
    void HOLD_예약은_CANCELED_될_수_있다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        reservationService.cancel(r.getId());

        assertThat(r.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    void CONFIRMED_예약도_CANCELED_될_수_있다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        reservationService.confirm(r.getId());
        reservationService.cancel(r.getId());

        assertThat(r.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }
}
