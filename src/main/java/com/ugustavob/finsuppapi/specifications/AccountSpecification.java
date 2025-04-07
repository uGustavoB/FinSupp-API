package com.ugustavob.finsuppapi.specifications;


import com.ugustavob.finsuppapi.dto.accounts.AccountFilterDTO;
import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

    public static Specification<AccountEntity> filter(AccountFilterDTO filterDTO) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filterDTO.getUserId() != null) {
                predicate = criteriaBuilder.and(predicate,criteriaBuilder.equal(root.get("user").get("id"), filterDTO.getUserId()));
            }

            if (filterDTO.getDescription() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + filterDTO.getDescription().toLowerCase() + "%"));
            }

            if (filterDTO.getBank() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("bank")), "%" + filterDTO.getBank().toLowerCase() + "%"));
            }

            if (filterDTO.getAccountType() != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("accountType"), filterDTO.getAccountType()));
            }

            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("description")));

            return predicate;
        };
    }
}
