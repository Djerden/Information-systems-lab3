package com.djeno.backend_lab1.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    // Метод для отправки уведомлений на канал
    public void sendNotification(String entityType, String action) {
        String message = entityType + " " + action; // Например: "study-groups created"
        messagingTemplate.convertAndSend("/topic/" + entityType, message); // Отправляем на канал /topic/{entityType}
    }
}