package com.back.domain.chat.chat.dto;

public record CreateChatRoomResBody(
        String message,
        Long chatRoomId
) {
}
