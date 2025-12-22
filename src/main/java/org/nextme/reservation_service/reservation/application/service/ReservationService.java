package org.nextme.reservation_service.reservation.application.service;

import org.nextme.reservation_service.reservation.domain.Reservation;
import org.nextme.reservation_service.reservation.presentation.PaymentConfirmRequest;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.nextme.reservation_service.reservation.presentation.ReservationCreateRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface ReservationService {

//    /**
//     * 예약을 생성하고 초기 상태(PENDING_PAYMENT)로 저장합니다.
//     * @param request 예약 생성에 필요한 데이터 DTO
//     * @return 생성된 예약의 ID
//     */
//    UUID createReservation(ReservationCreateRequest request);

    /**
     * 결제 성공 알림을 받아 예약을 확정 상태(CONFIRMED)로 변경합니다.
     * @param request 결제 ID, Room ID, 예약 ID를 포함하는 DTO
     * @return 확정된 예약 엔티티
     */
    Reservation confirmReservation(PaymentConfirmRequest request);

    /**
     * 예약 ID를 통해 예약을 취소 상태(CANCELLED)로 변경하고 환불에 필요한 결제 ID를 반환합니다.
     * @param paymentId 취소할 예약 ID
     * @return 환불 처리에 사용될 Payment ID
     */
    void cancelReservation(String paymentKey);

    /**
     * 예약 ID로 예약을 조회합니다.
     * @param reservationId 조회할 예약 ID
     * @return 조회된 예약 엔티티
     */
    Reservation getReservationById(UUID reservationId);

    List<Reservation> findAllByUserId(UUID userId);

    List<Reservation> findAllByAdvisorId(UUID advisorId);

    List<Reservation> findAll();

    /**
     * 결제 완료 이벤트 수신 시, 예약을 CONFIRMED 상태로 즉시 생성합니다.
     * @param event 결제 확정에 필요한 모든 데이터를 담고 있는 이벤트 DTO
     * @return 생성된 예약의 ID
     */
    UUID createConfirmedReservation(PaymentConfirmedEvent event);

    List<LocalTime> getOccupiedTimes(UUID productId, LocalDate date);
}
