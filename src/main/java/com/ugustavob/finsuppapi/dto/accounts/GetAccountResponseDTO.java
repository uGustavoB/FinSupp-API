package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;

public record GetAccountResponseDTO(
        String description,
        String bank,
        AccountType accountType,
        double balance
) {
}
