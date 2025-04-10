package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface SubscriptionRepository  extends JpaRepository<SubscriptionEntity, Integer>, JpaSpecificationExecutor<SubscriptionEntity> {
    @Query("SELECT s FROM SubscriptionEntity s WHERE s.card.account.user.id = :userId")
    Page<SubscriptionEntity> findAllByCard_Account_User_Id(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT COUNT(s) > 0 FROM SubscriptionEntity s WHERE s.card.account.id = :accountId")
    boolean existsByAccountId(@Param("accountId") Integer accountId);
}
