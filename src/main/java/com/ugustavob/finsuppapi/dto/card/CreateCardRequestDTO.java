package com.ugustavob.finsuppapi.dto.card;

import com.ugustavob.finsuppapi.entities.card.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CreateCardRequestDTO(
        @NotNull(message = "Card description cannot be null")
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
        @Min(value = 0, message = "Card limit must be greater than or equal to 0")
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
