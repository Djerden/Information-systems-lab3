package com.djeno.backend_lab1.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class InMemoryUserDetailsTest {

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Test
    public void testAdminUserExists() {
        // Проверяем, что пользователь admin существует
        var userDetails = inMemoryUserDetailsManager.loadUserByUsername("admin");
        assertNotNull(userDetails, "User admin should exist in in-memory store");
    }

    @Test
    public void testNonExistentUser() {
        // Проверяем, что несуществующий пользователь выбрасывает исключение
        assertThrows(UsernameNotFoundException.class, () ->
                inMemoryUserDetailsManager.loadUserByUsername("nonexistent")
        );
    }
}
