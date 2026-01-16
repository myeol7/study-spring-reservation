package com.example.studyspringreservation.reservation;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationRepository;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import com.example.studyspringreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ReservationOverlapConcurrencyTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void 겹치는_예약은_동시에_CONFIRMED_될_수_없다() throws Exception {
        Reservation r1 = reservationService.hold(
                1L, 10L,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2)
        );

        Reservation r2 = reservationService.hold(
                1L, 20L,
                LocalDateTime.now().plusHours(1).plusMinutes(30),
                LocalDateTime.now().plusHours(2).plusMinutes(30)
        );

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executor.submit(() -> {
            try {
                reservationService.confirm(r1.getId());
            } finally {
                latch.countDown();
            }
        });

        executor.submit(() -> {
            try {
                reservationService.confirm(r2.getId());
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        List<Reservation> confirmed =
                reservationRepository.findAll()
                        .stream()
                        .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                        .toList();

        assertThat(confirmed.size()).isLessThanOrEqualTo(1);
    }
}
