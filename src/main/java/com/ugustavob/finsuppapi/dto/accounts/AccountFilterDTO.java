package com.ugustavob.finsuppapi.dto.accounts;

import com.ugustavob.finsuppapi.entities.account.AccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AccountFilterDTO {
    private UUID userId;
    private String description;
    private String bank;
    private AccountType accountType;
}
