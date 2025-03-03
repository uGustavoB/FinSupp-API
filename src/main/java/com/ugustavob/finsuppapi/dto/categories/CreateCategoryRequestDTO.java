package com.ugustavob.finsuppapi.dto.categories;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryRequestDTO(
        @Length(min = 1, max = 20)
        @NotNull(message = "Category description is required")
        String description
) {
}
