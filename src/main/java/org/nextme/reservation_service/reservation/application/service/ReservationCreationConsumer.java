package org.nextme.reservation_service.reservation.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationCreationConsumer {

    private static final String TOPIC_PAYMENT_CONFIRMED = "payment.confirmed.v1";
    private final ObjectMapper om;

    private final ReservationService reservationService;

    @KafkaListener(topics = TOPIC_PAYMENT_CONFIRMED, groupId = "reservation-group")
    public void handlePaymentConfirmation(String payload) {
        try {
            PaymentConfirmedEvent event = om.readValue(payload, PaymentConfirmedEvent.class);

        log.info("결제 완료 이벤트 수신. 예약 생성 시작. User ID: {}", event.getUserId());

        reservationService.createConfirmedReservation(event);



        log.info("예약 ID {}가 성공적으로 생성 및 확정 완료되었습니다.", event.getUserId());


        } catch (JsonProcessingException e) {
            log.error("결제 완료 이벤트 payload 변환 실패: {}", e.getMessage(), e);
        }

    }
}
