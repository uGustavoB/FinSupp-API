package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.categories.CategoryFilterDTO;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
    public static Specification<CategoryEntity> filter(CategoryFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getDescription() != null && !filter.getDescription().isBlank()) {
                predicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getDescription().toLowerCase() + "%"
                );
            }

            if (filter.getId() != null) {
                predicate = criteriaBuilder.equal(root.get("id"), filter.getId());
            }

            return predicate;
        };
    }
}
