package com.ugustavob.finsuppapi.dto.categories;

import com.ugustavob.finsuppapi.entities.categories.CategoryVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryFilterDTO {
    private Integer id;
    private String description;
    private CategoryVisibility visibility;
}
