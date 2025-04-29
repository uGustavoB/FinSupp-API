package com.ugustavob.finsuppapi.repositories;


import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer>, JpaSpecificationExecutor<CategoryEntity> {
    Optional<CategoryEntity> findByDescription(String description);

    default Optional<CategoryEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<CategoryEntity> category = findById(id);

        if (category.isPresent()) {
            deleteById(id);
            return category;
        }

        return Optional.empty();
    }
}
