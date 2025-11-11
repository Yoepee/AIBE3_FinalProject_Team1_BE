package com.back.domain.chat.chat.controller;

import com.back.domain.chat.chat.dto.CreateChatRoomReqBody;
import com.back.domain.chat.chat.dto.CreateChatRoomResBody;
import com.back.domain.chat.chat.entity.ChatRoom;
import com.back.domain.chat.chat.service.ChatService;
import com.back.global.rsData.RsData;
import com.back.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ChatController {
    private final ChatService chatService;

    @PostMapping
    public RsData<CreateChatRoomResBody> createChatRoom(
            @RequestBody CreateChatRoomReqBody reqBody,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        ChatRoom chatRoom = chatService.createChatRoom(
                reqBody.memberId(),
                securityUser.getId()
        );

        return RsData.success("채팅방이 생성되었습니다.", new CreateChatRoomResBody(chatRoom.getName(), chatRoom.getId()));
    }

}
