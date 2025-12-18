package org.nextme.reservation_service.reservation.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.reservation_service.reservation.domain.Reservation;
import org.nextme.reservation_service.reservation.infrastructure.ReservationRepository;
import org.nextme.reservation_service.reservation.presentation.PaymentConfirmRequest;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.nextme.reservation_service.reservation.presentation.ReservationCreateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
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
    public void cancelReservation(String paymentKey) {
        Reservation reservation = reservationRepository.findByPaymentKey(paymentKey)
                .orElseThrow(() -> new ReservationNotFoundException("\"취소할 예약을 찾을 수 없습니다: \" + reservationId"));

        reservation.cancelReservation();

        UUID reservationId = reservation.getReservationId();
        if (reservation.isCancelled()) {
            log.warn("payment ID {}는 이미 취소 상태입니다. 중복 처리를 건너뜁니다.", paymentKey);

        }

        // 엔티티 내부의 비즈니스 로직(상태 변경) 호출
        //String paymentId = reservation.cancelReservation();

        // Dirty Checking을 통해 상태 변경 사항이 DB에 반영됩니다.
        log.info("예약 ID {} 취소가 완료되었습니다.", reservationId);
        //return paymentId; // 컨트롤러 또는 외부 호출자에게 환불 처리를 요청하도록 Payment ID 반환
    }

    /**
     * 예약 조회
     */
    @Override
    public Reservation getReservationById(UUID reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약 ID입니다: " + reservationId));
    }

    /**
     * 결제 완료 이벤트 수신 시, 예약을 CONFIRMED 상태로 즉시 생성합니다.
     */
    @Override
    @Transactional // 💡 Consumer가 호출하며, 여기서 트랜잭션이 시작됩니다.
    public UUID createConfirmedReservation(PaymentConfirmedEvent event) {

        UUID userUuid = UUID.fromString(event.getUserId());

        // 1. Reservation 엔티티 생성 (모든 확정 정보 포함, 상태는 CONFIRMED)
        Reservation reservation = Reservation.create(userUuid, UUID.randomUUID(), UUID.randomUUID(), event.getPaymentId());


        // 2. DB에 저장
        Reservation savedReservation = reservationRepository.save(reservation);

        return savedReservation.getReservationId();
    }

    @Override
    public List<LocalTime> getOccupiedTimes(UUID productId, LocalDate date) {

        // (상태가 'CANCELLED'인 것은 제외하고 'CONFIRMED', 'PENDING'인 것만 조회)
        List<Reservation> reservations = reservationRepository.findByProductIdAndReservationDateAndStatusIn(
                productId,
                date,
                List.of("CONFIRMED", "PENDING_PAYMENT")
        );

        // 2. 예약 객체에서 시작 시간(startTime)만 추출하여 리스트로 반환
        return reservations.stream()
                .map(reservation -> reservation.getStartTime()
                        .withSecond(0)  // 🌟 초를 0으로 강제 고정
                        .withNano(0))    // 🌟 나노초를 0으로 강제 고정
                .collect(Collectors.toList());
    }
}
