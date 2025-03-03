package com.ugustavob.finsuppapi.useCases.category;

import com.ugustavob.finsuppapi.exception.CategoryNotFoundException;
import com.ugustavob.finsuppapi.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public void execute(int id) {
        categoryRepository.deleteByIdAndReturnEntity(id).orElseThrow(CategoryNotFoundException::new);
    }
}
