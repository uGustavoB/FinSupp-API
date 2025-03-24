package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.bill.BillEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface BillRepository extends JpaRepository<BillEntity, Integer> {
    Optional<BillEntity> findById(Integer id);

    @Query("SELECT b FROM BillEntity b WHERE b.account.user.id = :userId")
    Page<BillEntity> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT b FROM BillEntity b WHERE b.account.user.id = :userId")
    Optional<BillEntity> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT b FROM BillEntity b WHERE b.account = :account AND b.startDate = :startDate AND b.endDate = " +
            ":endDate")
    BillEntity findByAccountAndDateRange(@Param("account") AccountEntity account,
                                                           @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    default Optional<BillEntity> deleteByIdAndReturnEntity(Integer id) {
        Optional<BillEntity> bill = findById(id);

        if (bill.isPresent()) {
            deleteById(id);
            return bill;
        }

        return Optional.empty();
    }
}
