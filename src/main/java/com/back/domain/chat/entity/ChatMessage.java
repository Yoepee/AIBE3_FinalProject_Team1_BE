package com.back.domain.chat.entity;

import com.back.global.jpa.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessage extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Column(name = "chat_member_id")
    private Long chatMemberId;

    public static ChatMessage create(String content, Long chatRoomId, Long chatMemberId) {
        return new ChatMessage(content, chatRoomId, chatMemberId);
    }
}
