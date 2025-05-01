package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;

import java.time.LocalDate;

public record TransactionResponseDTO(
        Integer id,
        String description,
        Double amount,
        boolean addToBill,
        Integer installments,
        LocalDate transactionDate,
        TransactionType type,
        Integer category,
        Integer accountId,
        Integer recipientAccountId
) {
}
