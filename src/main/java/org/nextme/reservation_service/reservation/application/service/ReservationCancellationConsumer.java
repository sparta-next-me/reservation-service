package org.nextme.reservation_service.reservation.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.common.event.PaymentCancelledEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationCancellationConsumer {

    private static final String TOPIC_PAYMENT_CANCELLED = "payment.cancelled.v1";
    private static final String GROUP_ID = "reservation-group";

    private final ObjectMapper om;
    private final ReservationService reservationService;

    @KafkaListener(topics = TOPIC_PAYMENT_CANCELLED, groupId = GROUP_ID)
    public void handlePaymentCancellation(String payload) {
        PaymentCancelledEvent event = null;
        try {
            event = om.readValue(payload, PaymentCancelledEvent.class);

            log.info("결제 취소 이벤트 수신. 예약 취소 시작. Payment ID: {}", event.getPaymentKey());

            // 1. 서비스 호출: 예외 발생 시 재시도 트리거
            reservationService.cancelReservation(event.getPaymentKey());

            log.info("예약 ID {}가 성공적으로 취소 완료되었습니다.", event.getPaymentKey());


        } catch (ReservationNotFoundException e) {
            // 🚨 핵심: 순서 오류 발생 (취소 메시지가 생성 메시지보다 먼저 도착)
            log.warn("Payment ID {}를 찾을 수 없습니다. (순서 뒤바뀜 가능성). 재시도합니다.",
                    event != null ? event.getPaymentKey() : "Unknown");

            // 예외를 던져 Kafka 컨테이너의 재시도 로직을 활성화합니다.
            // Kafka 설정에서 이 예외에 대한 재시도 정책이 정의되어 있어야 합니다.
            throw e;

        } catch (JsonProcessingException e) {
            // 파싱 오류는 재시도해도 실패하므로 DLT로 보내거나 로그만 남깁니다.
            log.error("결제 취소 이벤트 payload 변환 실패: {}", e.getMessage(), e);
            // 이 경우, 일반적으로 Consumer의 재시도 정책에서 제외됩니다.

        } catch (Exception e) {
            log.error("예약 ID {} 취소 처리 중 예상치 못한 오류 발생: {}",
                    event != null ? event.getReservationId() : "Unknown", e.getMessage(), e);
            throw new RuntimeException(e); // 다른 일반 예외도 재시도하도록 할 수 있습니다.
        }
    }
}
