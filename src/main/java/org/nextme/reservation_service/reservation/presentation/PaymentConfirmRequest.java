package org.nextme.reservation_service.reservation.presentation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentConfirmRequest {

    // 예약 엔티티를 찾기 위한 ID (API 요청 시 Path Variable이나 Body에 포함)
    private UUID reservationId;

    // 결제 시스템으로부터 받은 고유 ID
    private String paymentId;

    // 예약 확정 시 필요한 채팅방/세션 등의 ID
    private UUID roomId;

    @Builder // 필요한 경우 Builder 패턴으로 객체 생성
    public PaymentConfirmRequest(UUID reservationId, String paymentId, UUID roomId) {
        this.reservationId = reservationId;
        this.paymentId = paymentId;
        this.roomId = roomId;
    }
}
