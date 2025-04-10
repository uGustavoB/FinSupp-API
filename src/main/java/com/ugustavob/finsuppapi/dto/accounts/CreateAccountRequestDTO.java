package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequestDTO(
        @NotBlank(message = "Account description is required")
        @Schema(description = "Description of the account", example = "My credit account")
        String description,
        @NotNull(message = "Account type is required")
        @Schema(description = "Type of the account", example = "CHECKING")
        AccountType accountType,
        @NotBlank(message = "Bank is required")
        @Schema(description = "Bank of the account", example = "Banco do Brasil")
        String bank,
        @Min(value = 0, message = "Agency number must be greater than or equal to 0")
        @Schema(description = "Initial balance of the account", example = "1000.00")
        Double balance,
        @Min(value = 0, message = "Closing day must be between 1 and 31")
        @Max(value = 31, message = "Closing day must be between 1 and 31")
        @Schema(description = "Closing day of the account", example = "3")
        Integer closingDay,
        @Min(value = 0, message = "Payment due day must be between 1 and 31")
        @Max(value = 31, message = "Payment due day must be between 1 and 31")
        @Schema(description = "Payment due day of the account", example = "10")
        Integer paymentDueDay
) {
}
