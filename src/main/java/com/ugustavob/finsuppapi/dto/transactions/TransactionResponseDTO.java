package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;

import java.time.LocalDate;

public record TransactionResponseDTO(
        Integer id,
        String description,
        Double amount,
        Integer installments,
        LocalDate transactionDate,
        TransactionType type,
        Integer category,
        Integer cardId,
        Integer recipientAccountId
) {
}
