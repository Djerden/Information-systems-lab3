//package com.djeno.backend_lab1.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class WebSocketNotificationService {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public void sendNotification(String topic, Object message) {
//        messagingTemplate.convertAndSend(topic, message);
//    }
//}