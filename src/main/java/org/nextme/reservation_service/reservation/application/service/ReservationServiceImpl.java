package org.nextme.reservation_service.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.reservation_service.reservation.domain.Reservation;
import org.nextme.reservation_service.reservation.infrastructure.ReservationRepository;
import org.nextme.reservation_service.reservation.presentation.PaymentConfirmRequest;
import org.nextme.reservation_service.reservation.presentation.ReservationCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    /**
     * 예약 생성 (쓰기 작업이므로 @Transactional 필요)
     */
    @Override
    @Transactional
    public UUID createReservation(ReservationCreateRequest request) {
        // Reservation.create() 정적 팩토리 메소드를 사용하여 엔티티 생성
        Reservation reservation = Reservation.create(
                request.getUserId(),
                request.getAdvisorId(),
                request.getProductId(),
                request.getSagaId()
        );

        Reservation savedReservation = reservationRepository.save(reservation);
        return savedReservation.getReservationId();
    }

    /**
     * 예약 확정 (쓰기 작업이므로 @Transactional 필요)
     */
    @Override
    @Transactional
    public Reservation confirmReservation(PaymentConfirmRequest request) {
        Reservation reservation = getReservationById(request.getReservationId());

        // 엔티티 내부의 비즈니스 로직(상태 변경 및 필드 업데이트) 호출
        reservation.confirmReservation(request.getPaymentId(), request.getRoomId());

        // save를 명시적으로 호출하지 않아도 @Transactional에 의해 변경 사항이 DB에 반영됩니다 (Dirty Checking).
        return reservation;
    }

    /**
     * 예약 취소 (쓰기 작업이므로 @Transactional 필요)
     */
    @Override
    @Transactional
    public String cancelReservation(UUID reservationId) {
        Reservation reservation = getReservationById(reservationId);

        // 엔티티 내부의 비즈니스 로직(상태 변경) 호출
        String paymentId = reservation.cancelReservation();

        // Dirty Checking을 통해 상태 변경 사항이 DB에 반영됩니다.
        return paymentId; // 컨트롤러 또는 외부 호출자에게 환불 처리를 요청하도록 Payment ID 반환
    }

    /**
     * 예약 조회
     */
    @Override
    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 ID입니다: " + reservationId));
    }
}
