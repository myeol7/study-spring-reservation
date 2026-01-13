package com.example.studyspringreservation.reservation.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId; //좌석

    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    @Version
    private Long version;

    public void confirm(LocalDateTime now) {
        if (status != ReservationStatus.HOLD) {
            throw new ResolutionException("CONFIRM 불가 상태");
        }
        if (holdExpiresAt != null && holdExpiresAt.isBefore(now)) {
            throw new ResolutionException("HOLD 만료");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (status != ReservationStatus.HOLD) {
            throw new ResolutionException("CANCEL 불가 상태");
        }
        this.status = ReservationStatus.CANCELED;
    }

    public void expire(LocalDateTime now) {
        if (status == ReservationStatus.HOLD && holdExpiresAt != null && holdExpiresAt.isBefore(now)) {
            this.status = ReservationStatus.EXPIRED;
        }
    }

    public static Reservation hold(
            Long resourceId,
            String userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime holdExpiresAt
    ) {
        Reservation reservation = new Reservation();
        reservation.resourceId = resourceId;
        reservation.userId = userId;
        reservation.startAt = startAt;
        reservation.endAt = endAt;
        reservation.status = ReservationStatus.HOLD;
        reservation.holdExpiresAt = holdExpiresAt;
        return reservation;
    }

}