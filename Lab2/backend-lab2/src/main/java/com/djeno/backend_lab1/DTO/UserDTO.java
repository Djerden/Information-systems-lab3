package com.djeno.backend_lab1.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;

    public UserDTO() {
    }

    // Добавляем конструктор с аргументами
    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
