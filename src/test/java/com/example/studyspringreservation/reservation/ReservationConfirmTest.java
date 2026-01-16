package com.example.studyspringreservation.reservation;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import com.example.studyspringreservation.reservation.exception.ReservationException;
import com.example.studyspringreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
public class ReservationConfirmTest {

    @Autowired
    ReservationService reservationService;

    @Test
    void HOLD_상태의_예약은_CONFIRMED_될_수_있다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        reservationService.confirm(r.getId());

        assertThat(r.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void EXPIRED_예약은_CONFIRMED_될_수_없다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        r.expire(LocalDateTime.now().plusHours(1));

        assertThatThrownBy(() -> reservationService.confirm(r.getId()))
                .isInstanceOf(ReservationException.class);
    }
}
