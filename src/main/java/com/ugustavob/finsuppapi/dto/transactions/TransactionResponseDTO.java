package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;

import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDTO(
        Integer id,
        String description,
        Double amount,
        Integer installments,
        Boolean addToBill,
        LocalDate transactionDate,
        TransactionType type,
        Integer category,
        Integer accountUuid,
        Integer recipientAccountUuid
) {
}
