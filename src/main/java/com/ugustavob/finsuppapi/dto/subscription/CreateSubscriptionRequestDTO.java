package com.ugustavob.finsuppapi.dto.subscription;

import com.ugustavob.finsuppapi.entities.subscription.SubscriptionInterval;
import com.ugustavob.finsuppapi.entities.subscription.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record CreateSubscriptionRequestDTO(
        @NotBlank(message = "Description cannot be blank")
        @Schema(description = "Description of the subscription", example = "Netflix")
        @Length(min = 1, max = 30, message = "Description must be between 1 and 30 characters")
        String description,
        @Schema(description = "Price of the subscription", example = "29.90")
        @Min(value = 1, message = "Price must be at least 0")
        @Max(value = 999999, message = "Price must be less than 1 million")
        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double price,
        @Schema(description = "Interval of the subscription", example = "MONTHLY")
        @NotNull(message = "Interval cannot be null")
        SubscriptionInterval interval,
        @Schema(description = "Status of the subscription", example = "ACTIVE")
        @NotNull(message = "Status cannot be null")
        SubscriptionStatus status,
        @Schema(description = "Account ID associated with the subscription", example = "1")
        @Positive(message = "Account ID must be positive")
        @NotNull(message = "Account ID cannot be null")
        Integer accountId
//        @Schema(description = "Card ID associated with the subscription", example = "1")
//        @Positive(message = "Card ID must be positive")
//        @NotNull(message = "Account ID cannot be null")
//        Integer cardId
) {
}
