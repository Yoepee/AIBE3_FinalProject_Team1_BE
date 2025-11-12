package com.back.domain.chat.chat.repository;

import com.back.domain.chat.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // TODO : 추후 QueryDSL로 수정
    @Query("""
        SELECT cr
        FROM ChatRoom cr
        JOIN cr.chatMembers cm
        WHERE cr.post.id = :postId
          AND (cm.member.id = :hostId OR cm.member.id = :guestId)
        GROUP BY cr.id
        HAVING COUNT(cm) = 2
    """)
    Optional<ChatRoom> findByPostAndMembers(Long postId, Long hostId, Long guestId);
}
