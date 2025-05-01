package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.transactions.TransactionFilterDTO;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {

    public static Specification<TransactionEntity> filter(TransactionFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getUserId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("account").get("user").get("id"), filter.getUserId()));
            }

            if (filter.getAccountId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("account").get("id"), filter.getAccountId()));
            }

            if (filter.getDescription() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + filter.getDescription().toLowerCase() + "%"));
            }

            if (filter.getInstallments() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("installments"), filter.getInstallments()));
            }

            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                predicate = criteriaBuilder.between(root.get("transactionDate"), filter.getStartDate(), filter.getEndDate());
            }

            if (filter.getTransactionType() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("transactionType"), filter.getTransactionType()));
            }

            if (filter.getCategoryId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            if (filter.getCardId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("id"), filter.getCardId()));
            }

            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("transactionDate")));

            return predicate;
        };
    }
}
