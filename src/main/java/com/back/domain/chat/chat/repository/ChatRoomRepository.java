package com.back.domain.chat.chat.repository;

import com.back.domain.chat.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    @Query("""
        SELECT c FROM ChatRoom c
        WHERE EXISTS (
            SELECT m FROM c.chatMembers m WHERE m.member.id = :id1
        )
        AND EXISTS (
            SELECT m FROM c.chatMembers m WHERE m.member.id = :id2
        )
    """)
    Optional<ChatRoom> findByTwoMembers(@Param("id1") Long memberId1, @Param("id2") Long memberId2);


}
