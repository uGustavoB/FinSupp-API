package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;

import java.util.UUID;

public record AccountResponseDTO(
        UUID id,
        String description,
        String bank,
        AccountType accountType,
        double balance
) {
}
