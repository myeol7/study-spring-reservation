package com.example.studyspringreservation.reservation.service;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationRepository;
import com.example.studyspringreservation.reservation.domain.ReservationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public void confirm(Long reservationId, LocalDateTime now) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResolutionException("예약 없음"));

        reservation.confirm(now);

        boolean exists = reservationRepository
                .existsByResourceIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
                        reservation.getResourceId(),
                        ReservationStatus.CONFIRMED,
                        reservation.getEndAt(),
                        reservation.getStartAt()
                );

        if (exists) {
            throw new ResolutionException("이미 확정된 예약 존재");
        }
    }

}
