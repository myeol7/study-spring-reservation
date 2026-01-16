package com.example.studyspringreservation.reservation.service;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationRepository;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationExpireService {

    private final ReservationRepository reservationRepository;

    @Transactional
    public void expire() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> holds =
                reservationRepository.findAllByStatus(ReservationStatus.HOLD);

        holds.forEach(r -> r.expire(now));
    }
}
