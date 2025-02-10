package com.ugustavob.finsuppapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AssignRoleRequestDTO(
        @Schema(description = "Role", example = "ADMIN")
        @NotBlank(message = "Role is required")
        String role
) {
}
