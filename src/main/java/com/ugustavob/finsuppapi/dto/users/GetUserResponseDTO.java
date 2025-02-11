package com.ugustavob.finsuppapi.dto.users;

import java.util.UUID;

public record GetUserResponseDTO(
        UUID id,
        String name,
        String email
) {
}
