package com.example.studyspringreservation.reservation.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 아마 조건식의 의한 셀렉트 문으로서 존재하면 true를 뱉어주는 것 같음
    boolean existsByResourceIdAndStatusAndStartAtLessThanAndEndAtGreaterThan(
            Long resourceId,
            ReservationStatus status,
            LocalDateTime endAt,
            LocalDateTime startAt
    );

}
