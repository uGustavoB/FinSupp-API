package com.ugustavob.finsuppapi.useCases.transaction;

import com.ugustavob.finsuppapi.entities.transaction.TransactionEntityFinder;
import com.ugustavob.finsuppapi.entities.transaction.TransactionEntity;
import com.ugustavob.finsuppapi.exception.TransactionNotFoundException;
import com.ugustavob.finsuppapi.repositories.AccountRepository;
import com.ugustavob.finsuppapi.repositories.TransactionRepository;
import com.ugustavob.finsuppapi.services.BillService;
import com.ugustavob.finsuppapi.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final BillService billService;

    public void execute(Integer id) {
        TransactionEntity transaction = transactionRepository.findById(id).orElseThrow(TransactionNotFoundException::new);
        billService.revertTransactionBills(transaction);
        transactionRepository.delete(transaction);

        TransactionEntityFinder transactionEntityFinder =
                transactionService.getAndValidateTransactionEntities(transaction.getAccount(), transaction.getRecipientAccount());

        transactionEntityFinder = transactionService.revertAccountBalance(transaction, transactionEntityFinder);

        transactionService.saveAccounts(transactionEntityFinder, transaction.getTransactionType());
    }
}
