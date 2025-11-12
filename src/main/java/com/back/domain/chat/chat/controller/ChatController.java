package com.back.domain.chat.chat.controller;

import com.back.domain.chat.chat.dto.CreateChatRoomReqBody;
import com.back.domain.chat.chat.dto.CreateChatRoomResBody;
import com.back.domain.chat.chat.service.ChatService;
import com.back.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<CreateChatRoomResBody> createChatRoom(
            @RequestBody CreateChatRoomReqBody reqBody,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        CreateChatRoomResBody body = chatService.createChatRoom(
                reqBody.postId(),
                securityUser.getId()
        );

        return ResponseEntity.ok(body);
    }

}
