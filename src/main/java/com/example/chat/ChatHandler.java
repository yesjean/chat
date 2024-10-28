package com.example.chat;

import com.example.chat.entity.Message;
import com.example.chat.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChatHandler extends TextWebSocketHandler {
    private final Map<String, CopyOnWriteArrayList<WebSocketSession>> roomSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    public ChatHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String roomId = getRoomId(session);
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);

        // 방에 있는 이전 메시지를 불러오기
        List<Message> previousMessages = messageRepository.findByRoomId(roomId);
        for (Message msg : previousMessages) {
            String formattedMessage = msg.getUsername() + ": " + msg.getContent();
            try {
                session.sendMessage(new TextMessage(formattedMessage));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        Map<String, String> msgMap = objectMapper.readValue(message.getPayload(), Map.class);
        String username = msgMap.get("username");
        String chatMessage = msgMap.get("message");

        // 메시지 저장
        Message newMessage = new Message();
        newMessage.setRoomId(roomId);
        newMessage.setUsername(username);
        newMessage.setContent(chatMessage);
        newMessage.setTimestamp(LocalDateTime.now());
        messageRepository.save(newMessage);

        for (WebSocketSession webSocketSession : roomSessions.get(roomId)) {
            webSocketSession.sendMessage(new TextMessage(username + ": " + chatMessage));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String roomId = getRoomId(session);
        roomSessions.get(roomId).remove(session);
    }

    private String getRoomId(WebSocketSession session) {
        UriTemplate template = new UriTemplate("/chat/{roomId}");
        return template.match(session.getUri().getPath()).get("roomId");
    }
}



