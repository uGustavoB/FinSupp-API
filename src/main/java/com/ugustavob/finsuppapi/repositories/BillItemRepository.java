package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.bill.BillItemEntity;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BillItemRepository extends JpaRepository<BillItemEntity, Integer> {
    Optional<BillItemEntity> findById(Integer id);

    @Query("SELECT bi FROM BillItemEntity bi WHERE bi.bill.id = :billId")
    Page<BillItemEntity> findByBillId(@Param("billId") Integer billId, Pageable pageable);

    @Query("SELECT bi FROM BillItemEntity bi WHERE bi.bill.id = :billId")
    Optional<BillItemEntity> findByBillId(@Param("billId") Integer billId);

    List<BillItemEntity> findByTransaction(TransactionEntity transaction);

    default Optional<BillItemEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<BillItemEntity> billItem = findById(id);

        if (billItem.isPresent()) {
            deleteById(id);
            return billItem;
        }

        return Optional.empty();
    }

    @Query("SELECT bi FROM BillItemEntity bi WHERE bi.subscription = :subscription AND bi.bill.status = 'OPEN'")
    List<BillItemEntity> findBySubscription(SubscriptionEntity subscription);

    @Query("SELECT bi FROM BillItemEntity bi WHERE bi.subscription.id = :subscriptionId AND bi.bill.status = 'OPEN'")
    Optional<BillItemEntity> findBySubscriptionId(@Param("subscriptionId") Integer subscriptionId);
}
