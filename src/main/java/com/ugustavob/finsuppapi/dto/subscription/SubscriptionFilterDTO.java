package com.ugustavob.finsuppapi.dto.subscription;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionFilterDTO {
    private UUID userId;
    private Integer accountId;
    private String description;
    private SubscriptionInterval interval;
    private SubscriptionStatus status;
    private Integer cardId;
}
