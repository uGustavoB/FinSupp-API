package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.subscription.SubscriptionFilterDTO;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class SubscriptionSpecification {

    public static Specification<SubscriptionEntity> filter(SubscriptionFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getUserId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("account").get("user").get("id"), filter.getUserId()));
            }

            if (filter.getAccountId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("account").get("id"), filter.getAccountId()));
            }

            if (filter.getDescription() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getInterval() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("interval"), filter.getInterval()));
            }

            if (filter.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getCardId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("id"), filter.getCardId()));
            }

            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("description")));

            return predicate;
        };
    }
}
