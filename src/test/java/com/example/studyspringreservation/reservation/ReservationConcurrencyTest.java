package com.example.studyspringreservation.reservation;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationRepository;
import com.example.studyspringreservation.reservation.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void test() throws Exception {
        //row 객체 생성
        Reservation reservation = Reservation.hold(
                1L,
                "testUser",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusMinutes(10)
        );
        //db에 저장
        reservation = reservationRepository.save(reservation);
        Long reservationId = reservation.getId();

        int threadCount = 2;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    reservationService.confirm(reservationId, LocalDateTime.now());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // then
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
}

