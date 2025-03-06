package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateTransactionRequestDTO(
        @NotBlank(message = "Transaction description is required")
        @Schema(description = "Description of the transaction", example = "Payment of the rent")
        String description,
        @NotNull(message = "Transaction amount is required")
        @Schema(description = "Amount of the transaction", example = "1000.00")
        Double amount,
        @Schema(description = "Date and time of the transaction", example = "2021-10-10")
        LocalDate transactionDate,
        @NotNull(message = "Transaction type is required")
        @Schema(description = "Type of the transaction", example = "WITHDRAW")
        TransactionType type,
        @NotNull(message = "Transaction category is required")
        @Schema(description = "Category of the transaction", example = "1")
        Integer category,
        @NotNull(message = "Account UUID is required")
        @Schema(description = "UUID of the account", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID accountUuid,
        @Schema(description = "UUID of the recipient account", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID recipientAccountUuid
) {
}
