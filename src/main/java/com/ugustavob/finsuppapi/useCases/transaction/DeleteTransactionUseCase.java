package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.entities.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.entities.transaction.TransactionType;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    public void execute(Integer id) {
        TransactionEntity transaction = transactionRepository.deleteByIdAndReturnEntity(id).orElseThrow(TransactionNotFoundException::new);

        TransactionEntityFinder transactionEntityFinder =
                transactionService.getAndValidateTransactionEntities(transaction.getAccount(), transaction.getRecipientAccount());

        transactionEntityFinder = transactionService.revertAccountBalance(transaction, transactionEntityFinder);

        transactionService.saveAccounts(transactionEntityFinder, transaction.getTransactionType());
    }
}
