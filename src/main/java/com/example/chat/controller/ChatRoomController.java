package com.example.chat.controller;

import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    // 방 생성 API
    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatRoom chatRoom) {
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        return ResponseEntity.ok(savedRoom);
    }
}
