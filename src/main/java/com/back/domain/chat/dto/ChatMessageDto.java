package com.back.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long authorId,
        String content,
        LocalDateTime createdAt
) {
}
