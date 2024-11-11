package com.djeno.backend_lab1.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


// Регистрация нового пользователя
@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {
    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 1, max = 50, message = "Имя пользователя должно содержать от 1 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 1, max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;
}
