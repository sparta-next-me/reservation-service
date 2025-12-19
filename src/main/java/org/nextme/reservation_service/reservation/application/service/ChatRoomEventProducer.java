package org.nextme.reservation_service.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.common.event.ChatRoom;
import org.nextme.common.event.PaymentConfirmedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_CHAT_CREATE = "chat.create";

    public void sendChatRoomCreatedEvent(UUID advisorId, UUID userId, LocalDateTime sDateTime,  LocalDateTime eDateTime, UUID reservationId) {
        ChatRoom chatRoom = new ChatRoom(advisorId, userId, sDateTime, eDateTime, reservationId);

        kafkaTemplate.send(TOPIC_CHAT_CREATE, reservationId.toString(), chatRoom);
    }
}
