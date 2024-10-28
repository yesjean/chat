package com.example.chat.repository;

import com.example.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // 기본 CRUD 메서드를 제공
    ChatRoom findByName(String name);
}
