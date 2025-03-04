package com.ugustavob.finsuppapi.dto.transactions;

import com.ugustavob.finsuppapi.entities.transaction.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponseDTO(
        Integer id,
        String description,
        Double amount,
        LocalDateTime createdAt,
        TransactionType type,
        Integer category,
        UUID accountUuid,
        UUID recipientAccountUuid
) {
}
