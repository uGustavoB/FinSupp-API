package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.card.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Integer>, JpaSpecificationExecutor<CardEntity> {
    @Query("SELECT c FROM CardEntity c JOIN AccountEntity a ON c.account.id = a.id WHERE c.type = 'DEBIT' AND a.id = ?1")
    Optional<CardEntity> findDebitCardByAccountId(Integer accountId);

    @Query("SELECT c FROM CardEntity c JOIN AccountEntity a ON c.account.id = a.id WHERE c.type = 'CREDIT' AND a.id = ?1")
    CardEntity findCreditCardByAccountId(Integer accountId);

    List<CardEntity> findAllByAccountId(Integer id);

    @Query("SELECT c FROM CardEntity c JOIN AccountEntity a ON c.account.id = a.id WHERE c.type = 'DEBIT' AND c.description = ?1")
    Optional<CardEntity> findByDescription(String description);
}
