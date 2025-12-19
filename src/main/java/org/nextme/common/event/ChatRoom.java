package org.nextme.common.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatRoom {
    private UUID advisorId;
    private UUID userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime sDateTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime eDateTime;
    private UUID reservationId;

    @Builder
    public ChatRoom(UUID advisorId, UUID userId, LocalDateTime sDateTime, LocalDateTime eDateTime, UUID reservationId) {
        this.advisorId = advisorId;
        this.userId = userId;
        this.sDateTime = sDateTime;
        this.eDateTime = eDateTime;
        this.reservationId = reservationId;
    }
}
