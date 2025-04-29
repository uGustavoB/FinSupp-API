package com.ugustavob.finsuppapi.dto.categories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryFilterDTO {
    private Integer id;
    private String description;
}
