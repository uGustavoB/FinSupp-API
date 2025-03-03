package com.ugustavob.finsuppapi.services;

import com.ugustavob.finsuppapi.dto.categories.CategoryResponseDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryEntity getCategoryById(@PathVariable int id) {
        return categoryRepository.findById(id).orElseThrow(CategoryNotFoundException::new);
    }

    public Page<CategoryResponseDTO> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CategoryEntity> categoriesPage = categoryRepository.findAll(pageable);

        return categoriesPage.map(category -> new CategoryResponseDTO(
                category.getId(),
                category.getDescription()
        ));
    }
}
