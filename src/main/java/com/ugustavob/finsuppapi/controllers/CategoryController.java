package com.ugustavob.finsuppapi.controllers;

import com.ugustavob.finsuppapi.dto.ErrorResponseDTO;
import com.ugustavob.finsuppapi.dto.SuccessResponseDTO;
import com.ugustavob.finsuppapi.dto.categories.CategoryResponseDTO;
import com.ugustavob.finsuppapi.dto.categories.CreateCategoryRequestDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.services.BaseService;
import com.ugustavob.finsuppapi.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
@Tag(name = "5. Categories", description = "Endpoints for categories management")
@RequestMapping("/categories")
public class CategoryController {
    private final BaseService baseService;
    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SuccessResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                                    {
                                                      "message": "Categories found",
                                                      "type": "Success",
                                                      "dataList": [
                                                        {
                                                          "id": 1,
                                                          "description": "Food"
                                                        },
                                                        {
                                                          "id": 2,
                                                          "description": "Transport"
                                                        },
                                                        {
                                                          "id": 3,
                                                          "description": "Health"
                                                        }
                                                      "pagination": {
                                                        "currentPage": 0,
                                                        "pageSize": 10,
                                                        "totalPages": 1,
                                                        "totalElements": 3
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                                    {
                                                      "code": 401,
                                                      "message": "Unauthorized",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Categories not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Categories not found",
                                            value = """
                                                    {
                                                      "code": 404,
                                                      "message": "Categories not found",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
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

    @Operation(summary = "Get category by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SuccessResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                                    {
                                                      "message": "Category found",
                                                      "type": "Success",
                                                      "data": {
                                                        "id": 1,
                                                        "description": "Food"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                                    {
                                                      "code": 401,
                                                      "message": "Unauthorized",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Category not found",
                                            value = """
                                                    {
                                                      "code": 404,
                                                      "message": "Category not found",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
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

    @Operation(summary = "Create a new category")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Category created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SuccessResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                                    {
                                                      "message": "Category created",
                                                      "type": "Success",
                                                      "data": {
                                                        "id": 1,
                                                        "description": "Food"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                                    {
                                                      "code": 401,
                                                      "message": "Unauthorized",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unprocessable Entity",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation error",
                                                      "type": "Error",
                                                      "dataList": [
                                                        {
                                                          "description": "description",
                                                          "field": "Category name must be between 1 and 20 characters"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CreateCategoryRequestDTO createCategoryRequestDTO,
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

    @Operation(summary = "Update a category")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SuccessResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                                    {
                                                      "message": "Category updated",
                                                      "type": "Success",
                                                      "data": {
                                                        "id": 1,
                                                        "description": "Food"
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                                    {
                                                      "code": 401,
                                                      "message": "Unauthorized",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Category not found",
                                            value = """
                                                    {
                                                      "code": 404,
                                                      "message": "Category not found",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Entity",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unprocessable Entity",
                                            value = """
                                                    {
                                                      "code": 422,
                                                      "message": "Validation error",
                                                      "type": "Error",
                                                      "dataList": [
                                                        {
                                                          "description": "description",
                                                          "field": "Category name must be between 1 and 20 characters"
                                                        }
                                                      ]
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearer")
    public ResponseEntity<SuccessResponseDTO<CategoryResponseDTO>> updateCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CreateCategoryRequestDTO updateCategoryRequestDTO,
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

    @Operation(summary = "Delete a category")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = SuccessResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Success",
                                            value = """
                                                    {
                                                      "message": "Category deleted",
                                                      "type": "Success"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Unauthorized",
                                            value = """
                                                    {
                                                      "code": 401,
                                                      "message": "Unauthorized",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = ErrorResponseDTO.class
                            ),
                            examples = {
                                    @ExampleObject(
                                            name = "Category not found",
                                            value = """
                                                    {
                                                      "code": 404,
                                                      "message": "Category not found",
                                                      "type": "Error"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
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
