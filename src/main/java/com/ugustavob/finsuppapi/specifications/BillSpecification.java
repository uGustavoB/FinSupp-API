package com.ugustavob.finsuppapi.specifications;

import com.ugustavob.finsuppapi.dto.bills.BillFilterDTO;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class BillSpecification {

    public static Specification<BillEntity> filter(BillFilterDTO filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getUserId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("account").get("user").get("id"), filter.getUserId()));
            }

            if (filter.getId() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.equal(root.get("id"), filter.getId()));
            }

            if (filter.getStatus() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getAccountId() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("card").get("account").get(
                        "id"),
                        filter.getAccountId()));
            }

            if (filter.getMonth() != null) {
                predicate = criteriaBuilder.and(predicate,
                        getMonthPredicate(criteriaBuilder, root, "startDate", filter.getMonth()));
            }

            if (filter.getYear() != null) {
                predicate = criteriaBuilder.and(predicate,
                        getYearPredicate(criteriaBuilder, root, filter.getYear()));
            }

            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("startDate")));

            return predicate;
        };
    }

    private static Predicate getMonthPredicate(CriteriaBuilder criteriaBuilder, Root<BillEntity> root, String field, int month) {
        return criteriaBuilder.equal(
                criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("month"), root.get(field)),
                month
        );
    }

    private static Predicate getYearPredicate(CriteriaBuilder criteriaBuilder, Root<BillEntity> root, int year) {
        return criteriaBuilder.equal(
                criteriaBuilder.function("date_part", Integer.class, criteriaBuilder.literal("year"), root.get("startDate")),
                year
        );
    }
}
