package com.ugustavob.finsuppapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Schema(description = "User email", example = "admin@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotBlank(message = "Password is required")
        @Schema(description = "User password", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        String password) {
}
