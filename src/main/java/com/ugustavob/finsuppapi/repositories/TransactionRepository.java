package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

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
}
