package com.djeno.backend_lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // Максимальное количество одновременных задач
        executor.setMaxPoolSize(5);   // Максимальное количество потоков
        executor.setQueueCapacity(150); // Количество задач в очереди, ожидающих своей обработки
        executor.setThreadNamePrefix("import-task-");
        executor.initialize();
        return executor;
    }
}
