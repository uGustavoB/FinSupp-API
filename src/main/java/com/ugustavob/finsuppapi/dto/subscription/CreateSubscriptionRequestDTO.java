package com.ugustavob.finsuppapi.dto.subscription;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubscriptionRequestDTO(
        @NotBlank(message = "Description cannot be blank")
        String description,
        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double price,
        @NotNull(message = "Interval cannot be null")
        SubscriptionInterval interval,
        @NotNull(message = "Status cannot be null")
        SubscriptionStatus status,
        @Positive(message = "Card ID must be positive")
        @NotNull(message = "Account ID cannot be null")
        Integer cardId
) {
}
