package org.nextme.reservation_service.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.nextme.common.event.ChatRoom;
import org.nextme.common.event.ChatRoomCancel;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomCancelEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_CHAT_CANCEL = "chat.cancel";

    public void sendChatRoomCancelEvent(UUID reservationId) {
        ChatRoomCancel chatRoom = new ChatRoomCancel(reservationId);

        kafkaTemplate.send(TOPIC_CHAT_CANCEL, reservationId.toString(), chatRoom);
    }
}
