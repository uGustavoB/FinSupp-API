package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
    Optional<TransactionEntity> findById(Integer id);

    default Optional<TransactionEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<TransactionEntity> transaction = findById(id);

        if (transaction.isPresent()) {
            deleteById(id);
            return transaction;
        }

        return Optional.empty();
    }

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.user.id = :userId")
    Page<TransactionEntity> findByUserId(@Param("userId") UUID userId, Pageable pageable);
}
