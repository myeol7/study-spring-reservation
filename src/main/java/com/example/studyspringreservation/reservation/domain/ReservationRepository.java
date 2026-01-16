package com.example.studyspringreservation.reservation.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * CONFIRM 시점 겹침 검사 (비관락)
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select r from Reservation r
        where r.resourceId = :resourceId
          and r.status = 'CONFIRMED'
          and r.startAt < :endAt
          and r.endAt   > :startAt
    """)
    List<Reservation> findOverlappingConfirmedForUpdate(
            Long resourceId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

    List<Reservation> findAllByStatus(ReservationStatus status);
}
