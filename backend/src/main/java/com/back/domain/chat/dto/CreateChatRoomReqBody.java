package com.back.domain.chat.dto;

import jakarta.validation.constraints.NotNull;

public record CreateChatRoomReqBody(
        @NotNull
        Long postId
) {
}
