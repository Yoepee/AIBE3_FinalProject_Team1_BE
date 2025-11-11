package com.back.domain.chat.chat.service;

import com.back.domain.chat.chat.entity.ChatRoom;
import com.back.domain.chat.chat.repository.ChatMemberRepository;
import com.back.domain.chat.chat.repository.ChatRoomRepository;
import com.back.domain.member.member.entity.Member;
import com.back.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final MemberService memberService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMemberRepository chatMemberRepository;

    //TODO : postID 받도록 변경
    public ChatRoom createChatRoom(Long memberId1, Long memberId2) {
        Member member1 = memberService.getById(memberId1);
        Member member2 = memberService.getById(memberId2);

        Optional<ChatRoom> existingRoom = chatRoomRepository
                .findByTwoMembers(memberId1, memberId2);

        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        String roomName = member1.getNickname() + "&" + member2.getNickname();

        ChatRoom chatRoom = ChatRoom.builder()
                .name(roomName)
                .build();

        chatRoom.addMember(member1);
        chatRoom.addMember(member2);

        return chatRoomRepository.save(chatRoom);
    }
}
