package org.nextme.common.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class ChatRoomCancel {
    UUID reservationId;

    @Builder
    public ChatRoomCancel(UUID reservationId) {
        this.reservationId = reservationId;
    }
}
