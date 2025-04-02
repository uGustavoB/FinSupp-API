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
    private final CardEntity card;
    private AccountEntity recipientAccount;
    private final CategoryEntity category;

    public TransactionEntityFinder(CardEntity card, AccountEntity recipientAccount) {
        this.card = card;
        this.recipientAccount = recipientAccount;
        this.category = null;
    }
}
