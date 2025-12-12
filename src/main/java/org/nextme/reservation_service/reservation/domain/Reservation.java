package org.nextme.reservation_service.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nextme.common.jpa.JpaAudit;
import org.nextme.reservation_service.reservation.domain.ReservationStatus;

import java.util.UUID;

@Entity
@Table(name = "p_advisor_reservation")
@Getter
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends JpaAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reservationId;

    private UUID userId;

    private UUID advisorId;

    private UUID productId;

    private String sagaId;

    // 결제 성공 후 설정됨
    private String paymentId;

    private String paymentKey;

    // 확정 후 설정됨
    private UUID roomId;

    @Enumerated(EnumType.STRING) // Enum 타입을 DB에 문자열로 저장하기 위해 추가
    private ReservationStatus status;

    private boolean isCancelled = false;


    // --- 정적 팩토리 메소드 수정 ---
    /**
     * 예약을 생성하고 초기 상태(PENDING_PAYMENT)를 설정합니다.
     * 결제 및 확정 관련 필드(paymentId, roomId)는 제외합니다.
     */
    public static Reservation create(UUID userId, UUID advisorId, UUID productId, String paymentKey) {
       Reservation r = new Reservation();

       r.userId = userId;
       r.advisorId = advisorId;
       r.productId = productId;
       r.paymentKey = paymentKey;

        return r;
    }


    // --- 상태 변경 메소드 ---

    /**
     * 결제 ID와 Room ID를 할당하고 상태를 CONFIRMED로 변경합니다.
     * 결제 성공 후 호출됩니다.
     * @param paymentKey 결제 고유 ID
     * @param roomId 생성된 채팅방 또는 세션 ID
     */
    public void confirmReservation(String paymentKey, UUID roomId) {
        if (this.status != ReservationStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("예약 상태가 결제 대기 상태가 아닙니다. 현재 상태: " + this.status.getDescription());
        }
        this.paymentKey = paymentKey;
        this.roomId = roomId;
        this.status = ReservationStatus.CONFIRMED;
    }

    /**
     * 예약을 취소 상태로 변경하고 환불 처리를 위해 Payment ID를 반환합니다.
     */
    public String cancelReservation() {
        if (this.status == ReservationStatus.COMPLETED || this.status == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("이미 완료되었거나 취소된 예약은 취소할 수 없습니다.");
        }
        this.status = ReservationStatus.CANCELLED;
        return this.paymentKey; // 환불 로직에 필요
    }

    /**
     * 서비스 이용 완료 후 상태를 COMPLETED로 변경합니다.
     */
    public void completeReservation() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("예약이 확정 상태가 아니므로 완료할 수 없습니다.");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    /**
     * 결제 실패 시 상태를 PAYMENT_FAILED로 변경합니다.
     */
    public void failPayment() {
        this.status = ReservationStatus.PAYMENT_FAILED;
    }

    /**
     * 예약 만료 또는 노쇼 처리 시 상태를 변경합니다.
     */
    public void markAs(ReservationStatus newStatus) {
        if (newStatus != ReservationStatus.EXPIRED && newStatus != ReservationStatus.NO_SHOW) {
            throw new IllegalArgumentException("이 메소드는 EXPIRED 또는 NO_SHOW 상태로만 변경할 수 있습니다.");
        }
        this.status = newStatus;
    }
}
