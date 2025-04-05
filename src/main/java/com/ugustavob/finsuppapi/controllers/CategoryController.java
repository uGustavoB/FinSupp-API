package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.categories.CategoryResponseDTO;
import com.ugustavob.finsuppapi.dto.categories.CreateCategoryRequestDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@Tag(name = "Categories", description = "Endpoints for categories management")
@RequestMapping("/categories")
public class CategoryController {
    private final BaseService baseService;
    private final CategoryService categoryService;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<?> getCategories(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        Page<CategoryResponseDTO> categoriesPage = categoryService.getAllCategories(page, size);

        if (categoriesPage == null || categoriesPage.isEmpty()) {
            throw new CategoryNotFoundException("Categories not found");
        }

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Categories found",
                categoriesPage
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<CategoryResponseDTO>> getCategoryById(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CategoryEntity categoryEntity = categoryService.getCategoryById(id);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Category found",
                new CategoryResponseDTO(categoryEntity.getId(), categoryEntity.getDescription())
        ));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<CategoryResponseDTO>> createCategory(
            @RequestBody CreateCategoryRequestDTO createCategoryRequestDTO,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CategoryEntity categoryEntity = categoryService.createCategory(createCategoryRequestDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(categoryEntity.getId())
                .toUri();

        return ResponseEntity.created(location).body(new SuccessResponseDTO<>(
                "Category created",
                new CategoryResponseDTO(categoryEntity.getId(), categoryEntity.getDescription())
        ));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<CategoryResponseDTO>> updateCategory(
            @PathVariable Integer id,
            @RequestBody CreateCategoryRequestDTO updateCategoryRequestDTO,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        CategoryEntity updatedCategory = categoryService.updateCategory(updateCategoryRequestDTO,
                categoryService.getCategoryById(id));

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Category updated",
                new CategoryResponseDTO(updatedCategory.getId(), updatedCategory.getDescription())
        ));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<String>> deleteCategory(
            @PathVariable Integer id,
            HttpServletRequest request
    ) {
        baseService.checkIfUuidIsNull((UUID) request.getAttribute("id"));

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(new SuccessResponseDTO<>(
                "Category deleted"
        ));
    }
}
