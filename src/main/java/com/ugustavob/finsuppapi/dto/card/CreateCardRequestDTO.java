package com.ugustavob.finsuppapi.dto.card;

import com.ugustavob.finsuppapi.entities.card.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record CreateCardRequestDTO(
        @NotNull(message = "Card description cannot be null")
        @Length(min = 1, max = 30, message = "Card description must be between 1 and 30 characters")
        @Schema(
                description = "Description of the card",
                example = "My Card"
        )
        String description,
        @NotNull(message = "Card number cannot be null")
        @Length(min = 4, max = 4, message = "Card number must be 4 digits")
        @Schema(
                description = "Last 4 digits of the card number",
                example = "1234"
        )
        String lastNumbers,
        @NotNull(message = "Card limit cannot be null")
        @Min(value = 1, message = "Card limit must be at least 1")
        @Max(value = 999999999, message = "Card limit must be less than 10 digits")
        @Schema(
                description = "Card limit",
                example = "1000"
        )
        Integer limit,
        @NotNull(message = "Card type cannot be null")
        @Schema(
                description = "Type of the card",
                example = "CREDIT",
                implementation = CardType.class
        )
        CardType cardType,
        @NotNull(message = "Account ID cannot be null")
        @Schema(
                description = "ID of the account associated with the card",
                example = "1"
        )
        Integer accountId
) {
}
