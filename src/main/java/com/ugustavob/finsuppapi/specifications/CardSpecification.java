package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.card.CardFilterDTO;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class CardSpecification {

    public static Specification<CardEntity> filter(CardFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getUserId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("account").get("user").get("id"), filter.getUserId()));
            }

            if (filter.getDescription() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getLastNumbers() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("lastNumbers")), "%" + filter.getLastNumbers().toLowerCase() + "%"));
            }

            if (filter.getType() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("type"), filter.getType()));
            }

            if (filter.getAccountId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("account").get("id"), filter.getAccountId()));
            }

            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("description")));

            return predicate;
        };
    }
}
