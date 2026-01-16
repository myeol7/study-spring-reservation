package com.example.studyspringreservation.reservation.service;

import com.example.studyspringreservation.reservation.domain.Reservation;
import com.example.studyspringreservation.reservation.domain.ReservationRepository;
import com.example.studyspringreservation.reservation.exception.ReservationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * HOLD 생성
     */
    @Transactional
    public Reservation hold(
            Long resourceId,
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    ) {
        Reservation reservation = Reservation.hold(
                resourceId,
                userId,
                startAt,
                endAt,
                LocalDateTime.now().plusMinutes(5)
        );

        try {
            return reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException e) {
            throw new ReservationException("중복된 예약 요청입니다.");
        }
    }

    /**
     * 예약 확정
     */
    @Transactional
    public void confirm(Long reservationId) {
        Reservation target = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("예약이 존재하지 않습니다."));

        // 상태 검증
        target.confirm(LocalDateTime.now());

        // 겹침 검사 + 락
        List<Reservation> overlaps =
                reservationRepository.findOverlappingConfirmedForUpdate(
                        target.getResourceId(),
                        target.getStartAt(),
                        target.getEndAt()
                );

        if (!overlaps.isEmpty()) {
            throw new ReservationException("이미 해당 시간에 예약이 존재합니다.");
        }

        reservationRepository.save(target);
    }

    /**
     * 예약 취소
     */
    @Transactional
    public void cancel(Long reservationId) {
        Reservation target = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException("예약이 존재하지 않습니다."));
        target.cancel();
    }
}
