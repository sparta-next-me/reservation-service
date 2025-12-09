package org.nextme.reservation_service.reservation.infrastructure;

import org.nextme.reservation_service.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * 결제 ID를 기준으로 예약을 조회합니다.
     * 결제 서비스와의 연동이나 취소 시 예약 건을 찾기 위해 사용될 수 있습니다.
     * @param paymentId 결제 고유 ID
     * @return 해당 paymentId를 가진 Reservation 객체
     */
    Optional<Reservation> findByPaymentId(String paymentId);

    /**
     * Saga ID (분산 트랜잭션 ID)를 기준으로 예약을 조회합니다.
     * Saga 패턴을 사용하는 경우, 분산 트랜잭션의 상태 확인을 위해 사용될 수 있습니다.
     * @param sagaId Saga 고유 ID
     * @return 해당 sagaId를 가진 Reservation 객체
     */
    Optional<Reservation> findBySagaId(String sagaId);
}
