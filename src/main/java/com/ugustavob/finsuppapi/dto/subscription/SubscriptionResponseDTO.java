package com.ugustavob.finsuppapi.dto.subscription;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;

public record SubscriptionResponseDTO(
    Integer id,
    String description,
    double price,
    SubscriptionInterval interval,
    SubscriptionStatus status,
    Integer accountId
) {
}
