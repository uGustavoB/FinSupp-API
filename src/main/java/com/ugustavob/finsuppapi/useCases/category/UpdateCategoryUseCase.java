package com.ugustavob.finsuppapi.useCases.category;

import com.ugustavob.finsuppapi.dto.categories.CreateCategoryRequestDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CategoryEntity execute(CreateCategoryRequestDTO updateCategoryRequestDTO, CategoryEntity oldCategory) {
        oldCategory.setDescription(StringFormatUtil.toTitleCase(updateCategoryRequestDTO.description()));

        return categoryRepository.save(oldCategory);
    }
}
