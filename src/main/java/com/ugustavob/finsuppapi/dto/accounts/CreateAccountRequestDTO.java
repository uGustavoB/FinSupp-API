package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

public record CreateAccountRequestDTO(
        @NotBlank(message = "Account description is required")
        @Schema(description = "Description of the account", example = "My credit account")
        @Length(min = 1, max = 30, message = "Description must be between 1 and 30 characters")
        String description,
        @NotNull(message = "Account type is required")
        @Schema(description = "Type of the account", example = "CHECKING")
        AccountType accountType,
        @NotBlank(message = "Bank is required")
        @Schema(description = "Bank of the account", example = "Banco do Brasil")
        @Length(min = 1, max = 30, message = "Bank name must be between 1 and 30 characters")
        String bank,
        @Positive(message = "Balance must be positive")
        @Min(value = 0, message = "Balance must be positive")
        @Max(value = 999999999, message = "Balance must be less than 1 billion")
        @Schema(description = "Initial balance of the account", example = "1000.00")
        Double balance,
        @Min(value = 1, message = "Closing day must be between 1 and 31")
        @Max(value = 31, message = "Closing day must be between 1 and 31")
        @Schema(description = "Closing day of the account", example = "3")
        Integer closingDay,
        @Min(value = 1, message = "Payment due day must be between 1 and 31")
        @Max(value = 31, message = "Payment due day must be between 1 and 31")
        @Schema(description = "Payment due day of the account", example = "10")
        Integer paymentDueDay
) {
}
