package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.categories.CategoryFilterDTO;
import com.ugustavob.finsuppapi.dto.categories.CreateCategoryRequestDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.exception.BusinessException;
import com.ugustavob.finsuppapi.exception.CategoryDescriptionAlreadyExistsException;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import com.ugustavob.finsuppapi.specifications.CategorySpecification;
import com.ugustavob.finsuppapi.utils.StringFormatUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TransactionService transactionService;

    public CategoryEntity getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    public List<CategoryEntity> getAll(CategoryFilterDTO filterDTO) {
        Specification<CategoryEntity> specification = CategorySpecification.filter(filterDTO);

        return categoryRepository.findAll(specification);
    }

    public CategoryEntity createCategory(@Valid CreateCategoryRequestDTO createCategoryRequestDTO) {
        Optional<CategoryEntity> category =
                categoryRepository.findByDescription(createCategoryRequestDTO.description());

        if (category.isPresent()) {
            throw new CategoryDescriptionAlreadyExistsException(createCategoryRequestDTO.description());
        }

        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setDescription(StringFormatUtil.toTitleCase(createCategoryRequestDTO.description()));

        return categoryRepository.save(newCategory);
    }

    public void deleteCategory(int id) {
        if (transactionService.isCategoryAssociatedWithTransaction(id)) {
            throw new BusinessException("Category is already linked to a transaction");
        }

        categoryRepository.deleteByIdAndReturnEntity(id).orElseThrow(CategoryNotFoundException::new);
    }

    public CategoryEntity updateCategory(CreateCategoryRequestDTO updateCategoryRequestDTO,
                                         CategoryEntity oldCategory) {
        String newDescription = StringFormatUtil.toTitleCase(updateCategoryRequestDTO.description());

        if (oldCategory.getDescription().equals(newDescription)) {
            throw new BusinessException("Category description is the same as the old one");
        }

        oldCategory.setDescription(newDescription);

        return categoryRepository.save(oldCategory);
    }
}
