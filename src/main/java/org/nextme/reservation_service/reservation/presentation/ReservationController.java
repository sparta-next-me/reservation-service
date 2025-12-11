package org.nextme.reservation_service.reservation.presentation;

import lombok.RequiredArgsConstructor;
import org.nextme.reservation_service.reservation.domain.Reservation;
import org.nextme.reservation_service.reservation.infrastructure.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationRepository reservationRepository;

    // --- 1. 예약 생성 (결제 대기 상태) ---
    /**
     * 새로운 예약을 생성하고 PENDING_PAYMENT 상태로 저장합니다.
     * 이후 결제 서비스로 전달되어 결제가 진행됩니다.
     * POST /v1/reservations
     */
    @PostMapping
    public ResponseEntity<UUID> createReservation(@RequestBody ReservationCreateRequest request) {
        Reservation reservation = Reservation.create(
                request.getUserId(),
                request.getAdvisorId(),
                request.getProductId(),
                request.getSagaId()
        );

        Reservation savedReservation = reservationRepository.save(reservation);
        return ResponseEntity.ok(savedReservation.getReservationId());
    }

    // --- 2. 예약 확정 (결제 성공 후) ---
    /**
     * 결제 서비스로부터 결제 성공 알림을 받아 예약을 CONFIRMED 상태로 변경합니다.
     * POST /v1/reservations/confirm
     */
    /*@PostMapping("/confirm")
    public ResponseEntity<String> confirmReservation(@RequestBody PaymentConfirmRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + request.getReservationId()));

        // 엔티티 내부의 비즈니스 메소드 호출
        reservation.confirmReservation(request.getPaymentId(), request.getRoomId());

        reservationRepository.save(reservation);
        return ResponseEntity.ok("Reservation confirmed successfully.");
    }*/

    // --- 3. 예약 취소 ---
    /**
     * 예약 ID로 예약을 찾아 CANCELLED 상태로 변경합니다. (환불 로직은 별도 처리 필요)
     * POST /v1/reservations/{reservationId}/cancel
     */
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelReservation(@PathVariable UUID reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation not found: " + reservationId));

        // 엔티티 내부의 비즈니스 메소드 호출
        String paymentIdForRefund = reservation.cancelReservation();

        reservationRepository.save(reservation);
        // 취소 후, paymentIdForRefund를 사용하여 환불 서비스에 요청하는 로직이 추가될 수 있습니다.
        return ResponseEntity.ok("Reservation cancelled. Payment ID for refund: " + paymentIdForRefund);
    }
}
