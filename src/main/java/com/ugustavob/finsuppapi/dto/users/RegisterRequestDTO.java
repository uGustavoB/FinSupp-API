package com.ugustavob.finsuppapi.dto.users;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record RegisterRequestDTO(
        @NotBlank(message = "Name is required")
        @Schema(description = "Name of the user", example = "Jesse Pinkman", requiredMode = Schema.RequiredMode.REQUIRED)
        @Length(min = 3, max = 50, message = "Name must have between 3 and 50 characters")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        @Schema(description = "Email of the user", example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,
        @NotBlank(message = "Password is required")
        @Length(min = 6, message = "Password must have at least 6 characters")
        @Schema(description = "Password of the user", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
