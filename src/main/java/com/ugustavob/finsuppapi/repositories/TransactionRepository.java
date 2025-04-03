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

    @Query("SELECT COUNT(b) > 0 FROM BillEntity b " +
            "JOIN BillItemEntity bi ON bi.bill.id = b.id " +
            "WHERE bi.transaction.id = :transactionId " +
            "AND b.status IN ('PAID', 'CLOSED', 'CANCELED')")
    boolean existsBillWithTransactionId(@Param("transactionId") Integer transactionId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.card.account.user.id = :userId")
    Page<TransactionEntity> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT t FROM TransactionEntity t WHERE t.card.account.user.id = :userId")
    Optional<TransactionEntity> findByUserId(Integer id);
}
