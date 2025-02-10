package com.ugustavob.finsuppapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record GetUserResponseDTO(
        UUID id,
        String name,
        String email
) {
}
