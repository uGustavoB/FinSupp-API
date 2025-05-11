package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDate;

public record CreateTransactionRequestDTO(
        @NotBlank(message = "Transaction description is required")
        @Schema(description = "Description of the transaction", example = "Payment of the rent")
        @Length(max = 30, message = "Description must be less than 30 characters")
        String description,
        @NotNull(message = "Transaction amount is required")
        @Positive(message = "Transaction amount must be greater than zero")
        @Schema(description = "Amount of the transaction", example = "1000.00")
        @Min(value = 1, message = "Transaction amount must be greater than zero")
        @Max(value = 999999999, message = "Transaction amount must be less than 1 billion")
        Double amount,
        @Schema(description = "Date and time of the transaction", example = "2021-10-10")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate transactionDate,
        @NotNull(message = "Transaction type is required. Allowed values: WITHDRAW, DEPOSIT, TRANSFER")
        @Schema(description = "Type of the transaction", example = "WITHDRAW", allowableValues = {"WITHDRAW", "DEPOSIT", "TRANSFER"})
        TransactionType type,
        @Schema(description = "Indicates whether the transaction should be added to the credit card bill", example = "true")
        boolean addToBill,
        @Nullable()
        @Schema(description = "Number of installments", example = "1", defaultValue = "1")
        @Max(value = 120, message = "Installments must be less than 120")
        Integer installments,
        @NotNull(message = "Transaction category is required")
        @Positive(message = "Category Id must be greater than zero")
        @Schema(description = "Category of the transaction", example = "1")
        Integer category,
        @Schema(description = "Id of the account", example = "1")
        Integer accountId,
        @Schema(description = "Id of the recipient account", example = "2")
        Integer recipientAccountId
) {
}
