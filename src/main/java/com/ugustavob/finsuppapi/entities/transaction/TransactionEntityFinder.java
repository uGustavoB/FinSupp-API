package com.ugustavob.finsuppapi.entities.transaction;

import com.ugustavob.finsuppapi.entities.account.AccountEntity;
import com.ugustavob.finsuppapi.entities.card.CardEntity;
import com.ugustavob.finsuppapi.entities.categories.CategoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransactionEntityFinder {
    private AccountEntity account;
    private AccountEntity recipientAccount;
    private boolean addToBill;
    private final CategoryEntity category;

    public TransactionEntityFinder(AccountEntity account , AccountEntity recipientAccount) {
        this.account = account;
        this.recipientAccount = recipientAccount;
        this.category = null;
    }

    public TransactionEntityFinder(AccountEntity account, AccountEntity recipientAccount, CategoryEntity category) {
        this.account = account;
        this.recipientAccount = recipientAccount;
        this.category = category;
    }
}
