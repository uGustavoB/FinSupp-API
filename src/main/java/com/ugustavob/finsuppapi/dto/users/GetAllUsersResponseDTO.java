package com.ugustavob.finsuppapi.dto.users;

import java.util.Set;
import java.util.UUID;

public record GetAllUsersResponseDTO(
        UUID id,
        String name,
        String email,
        Set<String> role
) {
}
