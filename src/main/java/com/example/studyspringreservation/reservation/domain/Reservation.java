package com.example.studyspringreservation.reservation.domain;

import com.example.studyspringreservation.reservation.exception.ReservationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "reservation",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_exact_request",
                        columnNames = {"resource_id", "start_at", "end_at", "user_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_id", nullable = false)
    private Long resourceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "hold_expires_at")
    private LocalDateTime holdExpiresAt;

    /* ======================
       생성
       ====================== */

    public static Reservation hold(
            Long resourceId,
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            LocalDateTime holdExpiresAt
    ) {
        Reservation r = new Reservation();
        r.resourceId = resourceId;
        r.userId = userId;
        r.startAt = startAt;
        r.endAt = endAt;
        r.holdExpiresAt = holdExpiresAt;
        r.status = ReservationStatus.HOLD;
        return r;
    }

    /* ======================
       상태 전이
       ====================== */

    public void confirm(LocalDateTime now) {
        if (status != ReservationStatus.HOLD) {
            throw new ReservationException("HOLD 상태가 아니면 확정할 수 없습니다.");
        }
        if (holdExpiresAt.isBefore(now)) {
            throw new ReservationException("예약이 만료되었습니다.");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void expire(LocalDateTime now) {
        if (status == ReservationStatus.HOLD && holdExpiresAt.isBefore(now)) {
            this.status = ReservationStatus.EXPIRED;
        }
    }

    public void cancel() {
        if (status == ReservationStatus.EXPIRED) {
            throw new ReservationException("만료된 예약은 취소할 수 없습니다.");
        }
        if (status == ReservationStatus.CANCELED) {
            return;
        }
        this.status = ReservationStatus.CANCELED;
    }

    /* ======================
       조회 헬퍼
       ====================== */

    public boolean isActive() {
        return status == ReservationStatus.HOLD || status == ReservationStatus.CONFIRMED;
    }
}
