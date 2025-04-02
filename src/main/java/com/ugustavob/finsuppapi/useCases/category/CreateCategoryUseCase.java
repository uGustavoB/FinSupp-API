package com.ugustavob.finsuppapi.useCases.category;

import com.ugustavob.finsuppapi.dto.categories.CreateCategoryRequestDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.exception.CategoryDescriptionAlreadyExistsException;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public CategoryEntity execute(@Valid CreateCategoryRequestDTO createCategoryRequestDTO) {
        Optional<CategoryEntity> category = categoryRepository.findByDescription(createCategoryRequestDTO.description());

        if (category.isEmpty()) {
            CategoryEntity newCategory = new CategoryEntity();
            newCategory.setDescription(StringFormatUtil.toTitleCase(createCategoryRequestDTO.description()));

            return categoryRepository.save(newCategory);
        }

        throw new CategoryDescriptionAlreadyExistsException(createCategoryRequestDTO.description());
    }
}
