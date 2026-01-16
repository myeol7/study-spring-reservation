package com.example.studyspringreservation.reservation;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import com.example.studyspringreservation.reservation.service.ReservationExpireService;
import com.example.studyspringreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationExpireTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationExpireService expireService;

    @Test
    void HOLD_상태는_시간이_지나면_EXPIRED_된다() {
        Reservation r = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        expireService.expire();

        assertThat(r.getStatus()).isIn(
                ReservationStatus.HOLD,
                ReservationStatus.EXPIRED
        );
    }
}
