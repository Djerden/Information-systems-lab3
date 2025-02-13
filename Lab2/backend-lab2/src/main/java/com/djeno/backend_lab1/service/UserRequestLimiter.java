package com.djeno.backend_lab1.service;

import com.djeno.backend_lab1.exceptions.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Service
public class UserRequestLimiter {

    private final ConcurrentHashMap<Long, Semaphore> userSemaphores = new ConcurrentHashMap<>();

    /**
     * Пытается получить разрешение на выполнение запроса для пользователя.
     * Если разрешение не получено, выбрасывает исключение.
     */
    public void acquirePermission(Long userId) {
        Semaphore semaphore = userSemaphores.computeIfAbsent(userId, k -> new Semaphore(2)); // Ограничение: 2 запроса одновременно
        if (!semaphore.tryAcquire()) {
            throw new TooManyRequestsException("User has too many concurrent requests.");
        }
    }

    /**
     * Освобождает разрешение для пользователя.
     */
    public void releasePermission(Long userId) {
        Semaphore semaphore = userSemaphores.get(userId);
        if (semaphore != null) {
            semaphore.release();
        }
    }
}