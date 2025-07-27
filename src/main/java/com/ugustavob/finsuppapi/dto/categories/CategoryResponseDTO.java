package com.ugustavob.finsuppapi.dto.categories;

import com.ugustavob.finsuppapi.entities.categories.CategoryVisibility;

public record CategoryResponseDTO(
        Integer id,
        String description,
        CategoryVisibility visibility
) {
}
