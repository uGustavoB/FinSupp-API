package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateAccountRequestDTO(
        @NotBlank(message = "Account description is required")
        @Schema(description = "Description of the account", example = "My savings account")
        String description,
        @NotBlank(message = "Account type is required")
        @Schema(description = "Type of the account", example = "SAVINGS")
        AccountType accountType,
        @NotBlank(message = "Bank is required")
        @Schema(description = "Bank of the account", example = "Banco do Brasil")
        String bank
) {
}
