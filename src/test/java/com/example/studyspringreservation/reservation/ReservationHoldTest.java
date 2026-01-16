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
public class ReservationHoldTest {

    @Autowired
    ReservationService reservationService;

    @Test
    void 예약을_HOLD_상태로_생성할_수_있다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        assertThat(r.getStatus()).isEqualTo(ReservationStatus.HOLD);
    }
}
