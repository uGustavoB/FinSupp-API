package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.banks.BankFilterDTO;
import com.ugustavob.finsuppapi.entities.bank.BankEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class BankSpecification {

    public static Specification<BankEntity> filter(BankFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getName() != null && !filter.getName().isBlank()) {
                predicate = criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + filter.getName().toLowerCase() + "%"
                );
            }

            if (filter.getId() != null) {
                predicate = criteriaBuilder.equal(root.get("id"), filter.getId());
            }

            return predicate;
        };
    }
}
