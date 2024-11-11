package com.djeno.backend_lab1.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Ответ об ошибке")
public class ErrorResponse {
    @Schema(description = "Код статуса HTTP", example = "409")
    private int status;

    @Schema(description = "Сообщение об ошибке", example = "Пользователь с таким именем уже существует")
    private String message;

    @Schema(description = "Время возникновения ошибки", example = "2024-11-11T13:45:30")
    private String timestamp;
}
