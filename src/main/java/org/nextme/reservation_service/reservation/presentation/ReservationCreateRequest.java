package org.nextme.reservation_service.reservation.presentation;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티와 달리 DTO는 NoArgsConstructor를 public으로 두기도 합니다.
public class ReservationCreateRequest {

    private UUID userId;
    private UUID advisorId;
    private UUID productId;
    private String sagaId; // 분산 트랜잭션 추적 ID (선택적)

    @Builder // 필요한 경우 Builder 패턴으로 객체 생성
    public ReservationCreateRequest(UUID userId, UUID advisorId, UUID productId, String sagaId) {
        this.userId = userId;
        this.advisorId = advisorId;
        this.productId = productId;
        this.sagaId = sagaId;
    }
}
