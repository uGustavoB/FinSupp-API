package com.ugustavob.finsuppapi.entities;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionEntityFinder {
    private final AccountEntity account;
    private final AccountEntity recipientAccount;
    private final CategoryEntity category;

    public TransactionEntityFinder(AccountEntity account, AccountEntity recipientAccount) {
        this.account = account;
        this.recipientAccount = recipientAccount;
        this.category = null;
    }
}
