package com.ugustavob.finsuppapi.dto.categories;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateCategoryRequestDTO(
        @Length(min = 1, max = 15, message = "Category name must be between 1 and 15 characters")
        @NotNull(message = "Category description is required")
        String description
) {
}
