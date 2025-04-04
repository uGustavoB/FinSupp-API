package com.ugustavob.finsuppapi.dto.subscription;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSubscriptionRequestDTO(
        @NotNull(message = "Description cannot be null")
        String description,
        @NotNull(message = "Price cannot be null")
        @Min(value = 1, message = "Price must be greater than zero")
        Double price,
        @NotNull(message = "Interval cannot be null")
        SubscriptionInterval interval,
        @NotNull(message = "Status cannot be null")
        SubscriptionStatus status,
        @NotNull(message = "Account ID cannot be null")
        Integer cardId
) {
}
