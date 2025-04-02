package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import com.ugustavob.finsuppapi.entities.bill.BillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaRepository<BillEntity, Integer>, JpaSpecificationExecutor<BillEntity> {
    Optional<BillEntity> findById(Integer id);

    @Query("SELECT b FROM BillEntity b WHERE b.card.account.user.id = :userId")
    Page<BillEntity> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT b FROM BillEntity b WHERE b.card.account.user.id = :userId")
    Optional<BillEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BillEntity b WHERE b.card.account = :account AND b.startDate = :startDate AND b.endDate = " +
            ":endDate")
    BillEntity findByAccountAndDateRange(@Param("account") AccountEntity account,
                                                           @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM BillEntity b WHERE b.status = :status AND b.endDate <= :today")
    Page<BillEntity> findBillsToClose(
            @Param("status") BillStatus status,
            @Param("today") LocalDate today,
            Pageable pageable);

    @Query("SELECT b FROM BillEntity b WHERE b.status = 'CLOSED' AND b.dueDate < :today")
    Page<BillEntity> findOverdueBills(
            @Param("today") LocalDate today,
            Pageable pageable
    );

    default Optional<BillEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<BillEntity> bill = findById(id);

        if (bill.isPresent()) {
            deleteById(id);
            return bill;
        }

        return Optional.empty();
    }
}
