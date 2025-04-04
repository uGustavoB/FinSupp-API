package com.ugustavob.finsuppapi.repositories;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository  extends JpaRepository<SubscriptionEntity, Integer> {
}
