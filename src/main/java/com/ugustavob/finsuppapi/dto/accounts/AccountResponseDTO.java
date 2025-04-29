package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;

import java.util.UUID;

public record AccountResponseDTO(
        Integer id,
        String description,
        Integer bank,
        AccountType accountType,
        int closingDay,
        int paymentDueDay,
        double balance
) {
}
